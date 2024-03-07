package com.leyou.item.vo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_spu")
public class SpuVo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String brandId;
    private String cid1;// 1级类目    分类查询时，需要转换为商品的名称
    private String cid2;// 2级类目
    private String cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间

}
