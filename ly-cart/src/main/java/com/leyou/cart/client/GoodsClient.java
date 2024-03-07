package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/2 13:56
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {



}
