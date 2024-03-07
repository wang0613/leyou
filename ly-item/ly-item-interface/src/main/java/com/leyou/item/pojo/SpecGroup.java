package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;


import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 商品规格组
 */
@Data
@Table(name = "tb_spec_group")
public class SpecGroup {

    @Id
    @KeySql(useGeneratedKeys =true)
    private Long id;

    private Long cid;

    private String name;


    //添加spuParam集合，用于一次性的查询
    @Transient //不是数据库的字段，不用于封装数据
    private List<SpecParam> params;


   // getter和setter省略
}