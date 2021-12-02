package com.leyou.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.pojo.UserInfo;
import com.leyou.props.JwtProperties;
import com.leyou.sdk.GeetestConfig;
import com.leyou.sdk.GeetestLib;
import com.leyou.sdk.GeetestLibResult;
import com.leyou.serivce.AuthService;
import com.leyou.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     * @param username
     * @param password
     * @param response
     * @param request
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String token = authService.login(username, password);
        // 将token写入cookie中,httpOnly 防止其它js解析
        //发送到客户端cookie保存
        CookieUtils.newBuilder(response).httpOnly().request(request)
                .build(cookieName, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册
     * @param session
     * @return
     * @throws JSONException
     */
    @GetMapping("register")
    public String register(HttpSession session) throws JSONException {
        GeetestLib gtLib = new GeetestLib(GeetestConfig.GEETEST_ID, GeetestConfig.GEETEST_KEY);
        String userId = "test";
        String digestmod = "md5";
        Map<String,String> paramMap = new HashMap<String, String>();
        paramMap.put("digestmod", digestmod);
        paramMap.put("user_id", userId);
        paramMap.put("client_type", "web");
        paramMap.put("ip_address", "127.0.0.1");
        GeetestLibResult result = gtLib.register(digestmod, paramMap);
        // 将结果状态写到session中，此处register接口存入session，后续validate接口会取出使用
        // 注意，此demo应用的session是单机模式，格外注意分布式环境下session的应用
        session.setAttribute(GeetestLib.GEETEST_SERVER_STATUS_SESSION_KEY, result.getStatus());
        session.setAttribute("userId", userId);
        // 注意，不要更改返回的结构和值类型
        return result.getData();
    }

    /**
     * 验证用户信息
     * @param token
     * @param response
     * @param request
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletResponse response,
            HttpServletRequest request
    ){
        if(StringUtils.isBlank(token)){
            System.out.println("token = " + token);
            // 如果没有token ，证明未登录，返回403
            throw  new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);
            // 刷新token
            String newToken= JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            CookieUtils.newBuilder(response).httpOnly().request(request)
                    .build(cookieName, newToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // token已过期，或token不存在，解析失败
            throw  new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }

}
