package com.leyou.search.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 生成索引库和映射，并将spu转化为goods，存储到es索引库
 * @date 2021/1/31 20:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {


    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;

    /**
     * 创建索引库和映射
     */
    @Test
    public void createIndex() {
        template.createIndex(Goods.class);

        template.putMapping(Goods.class);
    }

    /**
     * 将spu转换为goods，存储es索引库
     */
    @Test
    public void loadData() {

        int page = 1;
        int rows = 100;

        int size = 0;
        do {

            //查询spu
            PageResult<Spu> result = goodsClient.querySpuByPage(null, true, page, rows);
            //取出的当前页
            List<Spu> spuList = result.getItems();
            if (CollectionUtils.isEmpty(spuList)){
                break;
            }
            //构建goods
            List<Goods> goodsList = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());


            goodsRepository.saveAll(goodsList); //存入索引库

            //翻页
            page++;

            size = spuList.size();
        }while (size == 100);



    }


}