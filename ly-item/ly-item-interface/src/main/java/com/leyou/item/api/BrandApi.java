package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 20:03
 */
@RequestMapping("brand")
public interface BrandApi {


    /**
     * 根据品牌id 查询品牌
     *
     * @param id 品牌id
     * @return Brand
     */
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据id查询Brand
     * @param ids 批量查询
     * @return List<Brand>
     */
    @GetMapping("list")
    List<Brand> queryBrandListById(@RequestParam("ids") List<Long> ids);

}
