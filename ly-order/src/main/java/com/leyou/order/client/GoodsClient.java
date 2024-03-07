package com.leyou.order.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description:
 * @date 2021/3/5 20:45
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {


}
