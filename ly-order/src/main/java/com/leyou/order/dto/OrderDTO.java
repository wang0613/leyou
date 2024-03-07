package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 订单传输对象
 * @date 2021/3/3 21:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @NotNull
    private Long addressId; //收货人地址

    @NotNull
    private Integer paymentType; //付款类型

    @NotNull
    private List<CartDTO> carts; //订单详情


}
