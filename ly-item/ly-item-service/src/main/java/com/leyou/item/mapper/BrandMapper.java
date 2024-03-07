package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.leyou.common.mapper.BaseMapper;

import java.util.List;

/**
 * 品牌mapper，继承自定义Mapper
 * @author wang
 */
public interface BrandMapper extends BaseMapper<Brand> {


    /**
     * 向品牌和分类的中间表插入数据
     * @param cid 分类id
     * @param bid  品牌id
     * @return 1 成功
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 查询当前分类下的所有品牌
     * @param cid 分类id
     * @return  List<Brand>
     */
    //@Select("SELECT * FROM `tb_brand` where id in(SELECT brand_id from tb_category_brand where category_id = #{cid}) ")
    @Select("SELECT b.* FROM tb_brand b INNER JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryBrandByCid(@Param("cid") Long cid);


    /**
     * 删除中间表 数据
     * @param id 品牌id
     */
    @Delete("DELETE from tb_category_brand where brand_id = #{id}")
    void deleteCategoryBrandCount(@Param("id") Long id);
}
