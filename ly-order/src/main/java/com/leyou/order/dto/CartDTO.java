package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 商品传输对象
 * @date 2021/3/3 21:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long skuId; //商品skuId
    private Integer num; //购买数量


}
