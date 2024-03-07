package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @author wang
 */
@Data
@Table(name = "tb_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    //使用hibernate-validator进行服务端的校验
    @NotEmpty(message = "用户名不能为空") //非null
    @Length(min = 4, max = 30, message = "用户名只能在4~30位之间") //限制长度，错误信息
    private String username;

    @JsonIgnore //忽略此字段的序列化,不要返回到前端
    @Length(min = 4, max = 30, message = "密码只能在4~30位之间")
    private String password;

    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$",message = "手机号校验失败") //校验手机号，指定正则
    private String phone;
    // 创建时间
    private Date created;
    // 密码的盐值
    @JsonIgnore
    private String salt;
}