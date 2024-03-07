package com.leyou.page.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 20:08
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
