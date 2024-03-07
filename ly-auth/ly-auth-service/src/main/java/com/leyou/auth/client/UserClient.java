package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 远程调用user 微服务
 * @date 2021/2/25 16:37
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {



}
