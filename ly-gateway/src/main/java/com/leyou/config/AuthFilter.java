package com.leyou.config;

import com.leyou.common.utils.CookieUtils;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;        // 过滤器类型，前置过滤器
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1; // 过滤器顺序
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取请求url
        String path = request.getRequestURI();
        return !isAllowPath(path);  // 是否放行
    }


    /**
     * 白名单过滤
     * @param path
     * @return
     */
    private boolean isAllowPath(String path) {
        for (String allowPath : filterProperties.getAllowPaths()) {
            if(path.startsWith(allowPath)){ // 允许
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        Cookie[] cookies = request.getCookies();
        // 获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            // 解析token
            UserInfo user = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            // 校验权限 ...
        } catch (Exception e) {
            // 解析token失败，未登录 ， 拦截
            ctx.setSendZuulResponse(false);
            // 返回状态码
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}
