package com.leyou.service;

import com.leyou.interceptors.UserInterceptor;
import com.leyou.mapper.CartMapper;
import com.leyou.pojo.UserInfo;
import com.leyou.prop.Cart;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CartService {
    @Resource
    private CartMapper cartMapper;

    private static String KEY_PREFIX = "cart:uid:";

    /**
     * 新增购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取当前用户
        final UserInfo user = UserInterceptor.getUser();
    }
}
