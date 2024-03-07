package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 载荷对象
 * @author HAILONG_WANG
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String username;

    //载荷是可以解码的， 密码不能存放在载荷中


}