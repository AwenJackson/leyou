package com.leyou.client;

import com.leyou.apis.BrandsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 实现内部调用
 */
@FeignClient(name = "item-service",contextId = "BrandClient")
public interface BrandClient extends BrandsApi {

}
