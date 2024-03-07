package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description: 查询商品分类
 * @date 2021/1/31 19:19
 */
@FeignClient(value = "item-service")  //调用指定微服务的实现
public interface CategoryClient extends CategoryApi {



}
