package com.leyou.page.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author haiLong_wang
 * @version 1.0
 * @description:
 * @date 2021/1/31 20:09
 */
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationApi {
}
