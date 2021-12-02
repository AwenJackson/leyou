package com.leyou.client;

import com.leyou.apis.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 实现内部调用
 */
@FeignClient(name = "item-service",contextId = "SpecificationClient")
public interface SpecificationClient extends SpecificationApi {
}
