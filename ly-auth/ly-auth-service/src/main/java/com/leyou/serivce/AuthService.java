package com.leyou.serivce;

import com.leyou.clients.UserClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.pojo.UserInfo;
import com.leyou.props.JwtProperties;
import com.leyou.utils.JwtUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import pojo.User;

import javax.annotation.Resource;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private UserClient userClient;

    public String login(String username, String password) {
        User user = userClient.queryUser(username, password);
        if(user == null){
            throw  new LyException(ExceptionEnum.USER_PASSWORD_ERROR);
        }
        //生成Token

        return JwtUtils.generateToken(new UserInfo(user.getId(),username),jwtProperties.getPrivateKey(), jwtProperties.getExpire());
    }
}
