package pojo.apis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.User;

public interface UserApi {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    User queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    );
}
