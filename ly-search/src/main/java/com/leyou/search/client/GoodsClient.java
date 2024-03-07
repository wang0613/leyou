package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 19:53
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

    //继承api中的方法
}
