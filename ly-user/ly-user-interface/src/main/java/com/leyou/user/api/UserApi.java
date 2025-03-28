package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/2/25 16:30
 */
public interface UserApi {


    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return user对象
     */
    @GetMapping("query")
    User queryUser(@RequestParam("username") String username,
                   @RequestParam("password") String password);
}
