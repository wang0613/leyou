package com.leyou.common.vo;

import lombok.Data;

/**
 * @author 订单详情
 */
@Data
public class OrderDetail {

    private Long id; //订单详情id

    private Long orderId;// 订单id


    private Long skuId;// 商品id
    private Integer num;// 商品购买数量
    private String title;// 商品标题
    private Long price;// 商品单价
    private String ownSpec;// 商品规格数据
    private String image;// 商品图片


}
