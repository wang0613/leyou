package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父分类的id 查询子分类
     * @param pid
     * @return
     */
    public List<Category> queryCategoryByPid(Long pid) {

        //查询条件，mapper会把对象中的非空属性作为查询条件
        Category record = new Category();
        record.setParentId(pid);

        //使用通用mapper进行查询，传递对象
        //会自动将对象中的非空条件，作为参数，进行查询
        List<Category> categoryList = categoryMapper.select(record);
        //判断查询条件
        if (CollectionUtils.isEmpty(categoryList)) {

            //在restful风格中，没哟查询到返回404
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return categoryList;
    }

    /**
     * 根据品牌id查询对应的分类
     * @param bid
     * @return
     */
    public List<Category> queryByBrandId(Long bid) {
        List<Category> categories = categoryMapper.queryByBrandId(bid);

        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }

        return categories;
    }

    /**
     * 根据多个分类id查询分类集合
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids) {

        List<Category> categories = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return categories;
    }
}
