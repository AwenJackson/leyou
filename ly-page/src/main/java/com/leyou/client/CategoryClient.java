package com.leyou.client;

import com.leyou.apis.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 实现内部调用
 */
@FeignClient(name = "item-service",contextId = "CategoryClient")
public interface CategoryClient extends CategoryApi {

}
