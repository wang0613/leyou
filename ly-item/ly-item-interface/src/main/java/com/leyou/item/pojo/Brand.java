package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_brand")
@Data
public class Brand {
    @Id
    @KeySql(useGeneratedKeys = true) //主键自增
    private Long id;
    private String name;// 品牌名称
    private String image;// 品牌图片
    private Character letter; //品牌首字母
    // getter setter 略


}