package com.leyou.order.interceptor;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 因为很多东西都需要进行登录，直接编写springMvc的拦截器，进行统一登录校验
 * 同时把解析到的用户信息保存起来，供后续使用
 * @date 2021/3/1 20:03
 */
//实现拦截器接口 可以把拦截器存放发哦common中，只有在mvc中配置后拦截器才会生效
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    //创建线程域  存储user信息（线程内共享，请求到达controller后可以共享user）
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    //在添加拦截器的时候，将prop传入
    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //1.获取cookie（获取指定cookie名称的值）
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());

        try {
            //2.解析token（配合公钥）
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());

            //3.传递用户的信息(使用线程域存储，供其他controller读取)
            tl.set(userInfo);  //key就是当前线程，（指定这行代码的线程）

            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 解析用户身份失败，", e);
            //401 未登录或超时
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false; //拦截下来
        }

    }

    /**
     * 删除user
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();   //删除线程中的user信息
    }


    /**
     * 获取当前线程域中的用户数据
     * @return userInfo
     */
    public static UserInfo getUser(){
        return tl.get();
    }
}
