package com.leyou.auth.web;

import com.leyou.auth.config.JwtPorperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/2/25 15:34
 */
@RestController
@EnableConfigurationProperties(JwtPorperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    //cookie名称
    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @Autowired
    private JwtPorperties prop; //读取公钥和私钥的值

    /**
     * 登录授权token
     *
     * @param username 用户名
     * @param password 密码
     * @return 无返回值，只需将用户设置到cookie中即可
     */
    @PostMapping("login")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {


        //需要将生成的token存储到cookie中
        String token = authService.login(username, password);

        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.setCookie(request, response, cookieName,
                token, null, null, true);
        //cookie的默认maxAge为-1，浏览器关闭 cookie失效
        //因为我们生成的token没有中文，所以可以不设定encode

        //post请求没有返回值，可以使用NO_CONTENT
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登录状态
     * @param token cookie的值  @CookieValue(获取指定cookie名称的值) 或者使用@RequestHeader(value=Cookie)
     * @return userInfo.username
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(
            @CookieValue("LY_TOKEN")String token, HttpServletRequest request,
            HttpServletResponse response) {

        //获取请求头中cookie的信息
        //1.判断cookie的是否有值，没有值就没有登录，可以省略，没有token 解析失败
//        if (StringUtils.isBlank(token)){
//            //403 未授权
//            throw new LyException(ExceptionEnum.UNAUTHORIZED);
//        }

        try {
            //使用工具类     配合公钥进行解密cookie的值
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());


            //解析成功，刷次token，重新生成
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            //写回cookie中
            CookieUtils.setCookie(request, response, cookieName,
                    newToken, null, null, true);


            //已登录 返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }


}
