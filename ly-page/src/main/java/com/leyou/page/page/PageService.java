package com.leyou.page.page;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/2/14 20:37
 */
@Slf4j
@Service
public class PageService {


    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoriesClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private TemplateEngine engine;


    /**
     * 加载数据
     *
     * @param spuId id
     * @return Map<String, Object> 页面所需的全部数据
     */
    public Map<String, Object> loadModel(Long spuId) {

        Map<String, Object> map = new HashMap<>();


        //返回spu， spuDetail，skus，
        Spu spu = goodsClient.querySpuById(spuId);
        SpuDetail spuDetail = spu.getSpuDetail();
        List<Sku> skus = spu.getSkus();
        //brand，
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //categories
        List<Category> categories = categoriesClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //规格参数
        List<SpecGroup> specs = specificationClient.querySpecGroupAndParamByCid(spu.getCid3());


        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", specs);
        // 查询特殊规格参数
//        map.put("paramMap", );

        return map;

    }

    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> map = new HashMap<>();

        // 根据id查询spu对象
        Spu spu = this.goodsClient.querySpuById(spuId);

        // 查询spudetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);

        // 查询sku集合
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);

        // 查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoriesClient.queryCategoryByIds(cids).stream().map(Category::getName).collect(Collectors.toList());
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", cids.get(i));
            categoryMap.put("name", names.get(i));
            categories.add(categoryMap);
        }
        // 查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        // 查询规格参数组
        List<SpecGroup> groups = this.specificationClient.querySpecGroupAndParamByCid(spu.getCid3());

        // 查询特殊的规格参数
        List<SpecParam> params = this.specificationClient.queryParamsList(null, spu.getCid3(), null);
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });

        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;

    }

    /**
     * 生成html
     * @param spuId id
     */
    public void createHtml(Long spuId) {

        //1.创建Thymeleaf上下文对象
        Context context = new Context();
        context.setVariables(this.loadData(spuId)); //将数据存入上下文对象

        //2.输出流，存储到nginx的html目录下，修改配置，如果nginx中没有页面，再走微服务
        File dest = new File("D:\\WorkSpace", spuId + ".html");

        //可能存在
        if (dest.exists()){
            dest.delete();
        }
        try (PrintWriter writer = new PrintWriter(dest, "UTF-8")) {

            //3.使用engine生成Html
            engine.process("item", context, writer);
        } catch (Exception ex) {
            log.error("[静态页微服务] 生成静态页面异常！",ex);

        }

    }

    /**
     * 删除静态页
     * @param spuId
     */
    public void deleteHtml(Long spuId) {
        File dest = new File("D:\\WorkSpace", spuId + ".html");
        if (dest.exists()){
            dest.delete();
        }
    }
}