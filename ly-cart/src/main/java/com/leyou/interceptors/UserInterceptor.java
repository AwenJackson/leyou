package com.leyou.interceptors;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * 因为很多接口都需要进行登录，我们直接编写SpringMVC拦截器，进行统一登录校验。
 * 同时，我们还要把解析得到的用户信息保存起来，以便后续的接口可以使用。
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    //使用ThreadLocal来存储查询到的用户信息，线程内共享，因此请求到达`Controller`后可以共享Use
    private static  final  ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中的token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            // 解析token
            UserInfo user = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            // 传递用户信息
            tl.set(user);
            // 放行
            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 解析用户身份失败，", e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 最后用完后，一定要删除线程
        tl.remove();
    }

    public static UserInfo getUser(){
        return tl.get();
    }
}
