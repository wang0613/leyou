package com.leyou.auth.service;


import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtPorperties;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/2/25 15:36
 */
@Service
@EnableConfigurationProperties(JwtPorperties.class)
@Slf4j
public class AuthService {


    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtPorperties prop;

    /**
     * 授权登录
     *
     * @param username 用户名
     * @param password 密码
     */
    public String login(String username, String password) {
        try {
            //校验用户名和密码
            User user = userClient.queryUser(username, password);
            if (user == null) {
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }

            //基于查询到的信息，作为载荷，生成cookie,并反返回
            //并使用私钥进行加密
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username),
                    prop.getPrivateKey(), prop.getExpire());

            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或密码有误，用户名:{}",username,e);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD); //用户凭证生成失败
        }


    }

}
