package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 王海龙
 */
@Service
public class BrandService {


    @Autowired
    private BrandMapper brandMapper;

    /**
     * 分页查询品牌
     *
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        PageHelper.startPage(page, rows);
        /**
         * WHERE "name" like "%key%" or letter == 'key'
         */
        //过滤
        Example example = new Example(Brand.class); //创建条件

        if (StringUtils.isNotBlank(key)) {
            //过滤条件
            //使用范例创建条件       name为数据表的字段   value为查询条件
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key.toUpperCase());
            //传入的字母可能是小写，需要转换一下
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            //order  by  id DESC
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_QUERY_FAIL);
        }

        //内部进行了强转，使用分页助手的PageInfo进行
        PageInfo<Brand> pageInfo = new PageInfo<>(list);

        return new PageResult<>(pageInfo.getTotal(), list);
    }

    /**
     * 品牌的存储
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        brand.setId(null);
        //insertSelect:只新增对象中有的值
        int count = brandMapper.insert(brand);

        if (count != 1) {
            throw new LyException(ExceptionEnum.BRAND_SAVE_FAIL);
        }

        for (Long cid : cids) {
            count = brandMapper.insertCategoryBrand(cid, brand.getId());

            if (count != 1) {
                throw new LyException(ExceptionEnum.BRAND_SAVE_FAIL);
            }
        }


    }

    /**
     * 根据id 进行查询
     *
     * @param bid
     * @return
     */
    public Brand queryById(Long bid) {
        Brand brand = brandMapper.selectByPrimaryKey(bid);

        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        return brand;
    }

    /**
     * 根据cid查询品牌
     *
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {

        List<Brand> list = brandMapper.queryBrandByCid(cid);

        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        return list;
    }

    /**
     * 修改品牌信息
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {

        Long id = brand.getId();

        brandMapper.deleteCategoryBrandCount(id);

        //根据主键进行更新
        brandMapper.updateByPrimaryKeySelective(brand);
        cids.forEach(c -> {
            //重新插入中间表
            brandMapper.insertCategoryBrand(c, brand.getId());
        });


    }

    /**
     * 根据id查询Brand
     * @param ids 批量查询
     * @return List<Brand>
     */
    public List<Brand> queryByIds(List<Long> ids) {

        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)){
            throw  new LyException(ExceptionEnum.BRAND_QUERY_FAIL);
        }

        return brands ;
    }
}
