package com.leyou.client;

import com.leyou.apis.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 实现内部调用
 */
@FeignClient(name = "item-service",contextId = "GoodsClient")
public interface GoodsClient extends GoodsApi {
}
