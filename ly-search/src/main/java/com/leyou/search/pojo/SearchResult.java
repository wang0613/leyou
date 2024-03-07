package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 搜索返回结果PageResult增强
 * @date 2021/2/4 19:07
 */
@Data
public class SearchResult extends PageResult<Goods> {

    /*
    在原有PageResult基础上增加    分类集合和品牌集合
     */
    private List<Category> categories; //分类的待选项
    private List<Brand> brands;//品牌的待选项

    private List<Map<String, Object>> specs; //可选的规格参数 key及待选项

    public SearchResult() {

    }

    //全参的构造（包括父类）
    public SearchResult(Long total, Integer totalPage, List<Goods> items,
                        List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
