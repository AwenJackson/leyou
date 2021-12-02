package com.leyou.clients;

import org.springframework.cloud.openfeign.FeignClient;
import pojo.apis.UserApi;

@FeignClient(value = "user-service",contextId = "userClient")
public interface UserClient extends UserApi {

}
