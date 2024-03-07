package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 导入数据，将数据代入到索引库
 * @date 2021/1/31 20:35
 */
@Service
@Slf4j
public class SearchService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private ElasticsearchTemplate template;


    /**
     * 将spu封装为goods
     */
    public Goods buildGoods(Spu spu) {
        //1.查询分类
        List<Category> categories = categoryClient.queryCategoryByIds
                (Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }

        //拿到names
        List<String> names = categories.stream().map(Category::getName)
                .collect(Collectors.toList());

        //2.查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //1.查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_QUERY_FAIL);
        }
        //对sku进行处理 (不需要过多的数据)
        List<Map<String, Object>> skus = new ArrayList<>();
        //拿到价格
        Set<Long> priceSet = new TreeSet<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skus.add(map);

            priceSet.add(sku.getPrice()); //添加价格
        }

        // 查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParamsList(null, spu.getCid3(), true);
        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //获取通用规格参数,并转换为map
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有的规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {
                });


        //规格参数，key为规格参数的名字，值为规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            String key = param.getName();//规格名称

            Object value = "";
            //是不是通用属性
            if (param.getGeneric()) {
                value = genericSpec.get(param.getId());

                //特殊字段：数值类型
                if (param.getNumeric()) {
                    //处理成段
                    // 如果是数值的话，判断该数值落在那个区间
                    value = this.chooseSegment(value.toString(), param);
                }

            } else {
                value = specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key, value);
        }

        Goods goods = new Goods();
        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(all); // 搜索字段包括品牌，标题，分类，规格等
        goods.setPrice(priceSet); // 所有sku价格集合
        goods.setSkus(JsonUtils.serialize(skus)); // 所以sku的集合的json格式
        goods.setSpecs(specs);// 所有可搜索的规格参数 key在spec value在spu—detail

        return goods;
    }

    /**
     * 处理特殊字段 的区间值
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 搜索功能
     *
     * @param request key为查询条件
     * @return SearchResult
     */
    public PageResult<Goods> search(SearchRequest request) {

        //是否有搜索条件
        if (StringUtils.isBlank(request.getKey())) {
            return null;
        }

        int page = request.getPage() - 1; //获取分页
        int size = request.getSize(); //每页条数

        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        //0、 结果过滤 只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        //1、分页
        queryBuilder.withPageable(PageRequest.of(page, size));  //es中分页从0开始

        //2、过滤(对key进行全文检索查询)
        //搜索条件
        QueryBuilder baseQuery = this.buildBooleanQueryBuilder(request);
        queryBuilder.withQuery(baseQuery);


        //3.聚合(显示分类和品牌)
        //3.1聚合分类
        String categoryAggName = "category_agg";
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3")); //cid3 手机
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        //4、查询
//        Page<Goods> result = repository.search(queryBuilder.build());
        //4、聚合之后 进行查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);


        //5、解析结果
        //5.1分页结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //5.2 解析聚合结果（分类和品牌）
        Aggregations aggs = result.getAggregations();//获取所有的聚合结果

        //处理分类和品牌的聚合结果
        List<Category> categories = this.parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand> brands = this.parseBrandAgg(aggs.get(brandAggName));


        //6、准备可选规格参数以及待选项 (完成规格参数聚合，根据分类的聚合结果集为1)
        List<Map<String, Object>> specs = null;
        //6.1 当商品的分类聚合结果为1时，才聚合
        if (categories != null && categories.size() == 1) {
            //商品分类存在，并且数量为1，可以聚合规格参数
            //传递分类的id，并在原来的查询条件基础上进行规格参数的聚合
            specs = this.buildSpecificationAgg(categories.get(0).getId(), baseQuery);

        }


        return new SearchResult(total, totalPage, goodsList, categories, brands, specs);
    }

    /**
     * 构建bool查询构建器
     *
     * @param request
     * @return
     */
    private QueryBuilder buildBooleanQueryBuilder(SearchRequest request) {

        //创建bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();


        //1.查询条件作为must
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));

        //2.过滤字段，作为过滤条件
        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())) {
            return queryBuilder;
        }
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey(); //获取过滤字段名
            //key分为 分类和品牌
            if (!"cid3".equals(key) && !"brandId".equals(key)) {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }


            String value = entry.getValue(); //获取值

            queryBuilder.filter(QueryBuilders.termQuery(key, value));

        }

        return queryBuilder;
    }

    /***
     * 进行规格参数的聚合
     * @param cid 具体分类的id
     * @param baseQuery 基础的查询条件
     * @return List<Map < String, Object>> 可选的规格参数
     */
    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder baseQuery) {

        //定义一个集合，收集聚合结果集
        List<Map<String, Object>> specs = new ArrayList<>();
        //查询自定义结果的构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        //1.查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamsList(null, cid, true);

        //2. 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(baseQuery); //在原有的基础上进行聚合
        //添加聚合
        for (SpecParam param : params) {
            String name = param.getName(); //使用规格参数的名字作为聚合名称
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        }

        //3.获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //4.解析结果
        Aggregations aggs = result.getAggregations();

        for (SpecParam param : params) {
            String name = param.getName();
            StringTerms terms = aggs.get(name);

            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            List<String> options = terms.getBuckets().stream()
                    .map(b -> b.getKeyAsString()).collect(Collectors.toList());

            //准备map
            Map<String, Object> map = new HashMap<>();
            map.put("k", name); // 放入规格参数名
            map.put("options", options); // 收集规格参数值


            specs.add(map); //添加到map中
        }

        return specs;
    }

    /**
     * 处理Brand聚合
     *
     * @param agg 是Aggregation的实现类 因为聚合的结果BrandId是一个long类型
     * @return List<Brand>
     */
    private List<Brand> parseBrandAgg(LongTerms agg) {

        try {
            //拿到桶之后，使用stream流，取出key的值，转换为list集合
            List<Long> brandIds = agg.getBuckets().stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());


            //拿着所有的id，查询出一个list<Brand>，返回
            return brandClient.queryBrandListById(brandIds);
        } catch (Exception ex) {
            log.error("[搜索服务]查询品牌异常" + ex);
            return null;
        }

    }

    /**
     * 处理Category的集合
     *
     * @param agg 分类聚合agg
     * @return List<Category>
     */
    private List<Category> parseCategoryAgg(LongTerms agg) {

        try {
            List<Long> cIds = agg.getBuckets()
                    .stream().map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            return categoryClient.queryCategoryByIds(cIds);
        } catch (Exception ex) {
            log.error("[搜索服务]查询分类异常" + ex);
            return null;
        }

    }

    /**
     * 修改或者新增索引
     * @param spuId 商品id
     */
    public void createOrUpdateIndex(Long spuId) {

        //1.根据spuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //2.构建为goods，
        Goods goods = this.buildGoods(spu);

        //3.新增或者修改
        repository.save(goods);
    }

    /**
     * 删除索引库
     * @param spuId
     */
    public void deleteIndex(Long spuId) {

        repository.deleteById(spuId);
    }
}
