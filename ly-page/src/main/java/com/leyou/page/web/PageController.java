package com.leyou.page.web;

import com.leyou.page.page.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 商品详情页面微服务的数据填充和跳转
 * @date 2021/2/12 15:27
 */
@Controller //进行视图的跳转
public class PageController {


    @Autowired
    private PageService pageService;


    /**
     *  跳转到商品详情页
     * @param spuId 前端传递商品的spuID
     * @param model 模型
     * @return item.html
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model){

        //1.准备模型数据
        //全部需要的数据 -spu信息 -spu的详情 -spu下的所有sku -品牌 -商品三级分类 -商品规格参数、规格参数组

        Map<String,Object> attributes = pageService.loadData(spuId);

        //2.添加数据到模型视图
        model.addAllAttributes(attributes); //一次性添加
        //当我们将数据添加到model中时，spring mvc会将数据添加到Thymeleaf的上下文中Context

        //3.返回视图
        return "item";
    }



}
