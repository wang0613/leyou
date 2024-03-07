package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 菜单的mapper
 * 继承自mapper接口
 */
//SelectByIdListMapper(T,PK)来源于mapper，可以根据传入的ids集合，查询出分类集合
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category, Long> {


    @Select("SELECT * from tb_category where id in(SELECT category_id from tb_category_brand where brand_id  = #{bid})")
    List<Category> queryByBrandId(@Param("bid") Long bid);
}
