package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 商品的spu 通用属性
 */
@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId; //品牌id
    private Long cid1;// 1级类目    分类查询时，需要转换为商品的名称
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题

    //数据库中tinyint(1) 可以使用int或者boolean接收    0为false     1为true
    private Boolean saleable;// 是否上架
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间

    //当返回到页面时，忽略此字段
    @JsonIgnore
    private Date lastUpdateTime;// 最后修改时间

//我们页面要的不是分类的id 而是对应的名字，为了方便，直接加在pojo上， 企业中使用Vo扩展，进行转换

    //标注为不是 数据库字段
    @Transient
    private String cname;
    @Transient
    private String bname;

    @Transient
    private SpuDetail spuDetail;// 商品详情
    @Transient
    private List<Sku> skus;// sku列表
	// 省略getter和setter
}