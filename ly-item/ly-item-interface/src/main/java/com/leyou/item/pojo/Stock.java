package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 库存表
 */
@Table(name = "tb_stock")
@Data
public class Stock {

    @Id
    private Long skuId; //sku表的id
    private Integer seckillStock;// 秒杀可用库存
    private Integer seckillTotal;// 已秒杀数量
    private Integer stock;// 正常库存
}