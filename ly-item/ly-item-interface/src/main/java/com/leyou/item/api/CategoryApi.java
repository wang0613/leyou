package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 20:05
 */
public interface CategoryApi {


    /**
     * 根据分类ids 查询分类名称
     * @param ids 分类id
     * @return List<Category>
     */
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);


}
