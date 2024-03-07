package com.leyou.cart.pojo;

import lombok.Data;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 购物车实体类
 * @date 2021/3/2 12:17
 */
@Data
public class Cart {


    //skuId: this.sku.id, num: this.num
    private Long userId;// 用户id
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数


    //hash   Map<String,Map<String,String>>>>
    //第一层key为用户id
    //第二层key为商品ID value为购物车数据
}
