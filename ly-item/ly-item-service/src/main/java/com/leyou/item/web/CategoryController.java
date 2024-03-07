package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @description: 分类管理
 * @author: haiLong_wang
 * @time: 2021/1/12 21:10
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询商品分类
     * @param pid 父节点分类id
     * @return ResponseEntity<List<Category>> 返回restful风格
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryByPid(
            @RequestParam(value = "pid",defaultValue = "0") Long pid) {

        //可以使用简化写法  ok中存储需要返回的数据
        return ResponseEntity.ok(categoryService.queryCategoryByPid(pid));
    }

    /**
     * 根据品牌id查询对应的分类
     * @return List<Category>
     */
    @GetMapping("bid/{id}")
    public ResponseEntity<List<Category>> queryCategoryById(@PathVariable("id") Long bid) {

        return ResponseEntity.ok(categoryService.queryByBrandId(bid));
    }

    /**
     * 根据分类ids 查询分类名称
     * @param ids 分类id
     * @return List<Category>
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {

        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

}
