package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品通用属性 详情
 */
@Table(name = "tb_spu_detail")
@Data
public class SpuDetail {
    @Id
    private Long spuId;// 对应的SPU的id （和spu表一对一进行关联）
    private String description;// 商品描述
    private String genericSpec;// 商品的全局规格属性
    private String specialSpec;// 商品特殊规格的名称及可选值模板
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    // 省略getter和setter
}