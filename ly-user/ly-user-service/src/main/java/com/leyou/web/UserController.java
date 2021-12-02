package com.leyou.web;

import com.leyou.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pojo.User;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验。
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable String data,@PathVariable Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }

    /**
     * 发送短信
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        return ResponseEntity.ok(userService.queryUser(username,password));
    }

}
