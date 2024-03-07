package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @date 2021/2/22 15:37
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验用户数据
     * @param data 要校验的数据
     * @param type 1用户名 2手机号
     * @return true可用    false不可用
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable(value = "data") String data,
            @PathVariable(value = "type") Integer type) {

        return ResponseEntity.ok(userService.checkData(data, type));
    }

    /**
     * 根据手机号发送 6位纯数字验证码
     * @param phone 手机号
     * @return void
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendCode(
            @RequestParam("phone") String phone) {

        userService.sendCode(phone);


        //无返回值 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册    使用@Valid校验user中的信息是否符合规范   BindingResult当校验失败时，响应此类给客户端
     * @param user 用户属性
     * @param code 验证码
     * @return void
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code) {

//        if (result.hasErrors()){
//            //使用stream流，获取所有的错误字段的异常信息，使用joining指定的分隔符，拼接成一个字符串 进行返回
//            throw new RuntimeException(result.getFieldErrors().stream()
//                    .map(e->e.getDefaultMessage()).collect(Collectors.joining("|")));
//        }

        userService.register(user,code);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return user对象
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        return ResponseEntity.ok(userService.queryUser(username,password));
    }
}
