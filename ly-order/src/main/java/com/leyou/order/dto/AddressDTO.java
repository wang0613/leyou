package com.leyou.order.dto;

import lombok.Data;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 地址传输
 * @date 2021/3/4 17:58
 */
@Data
public class AddressDTO {

    private Long id;
    private String name; // 收货人全名
    private String phone; // 移动电话
    private String state; // 省份
    private String city; // 城市
    private String district; // 区/县
    private String address; // 收货地址，如：xx路xx号
    private String zipCode; // 邮政编码,如：310001
    private Boolean idDefault;
}
