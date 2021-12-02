package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CodecUtils;
import com.leyou.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pojo.User;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class UserService {
    //设置验证码前缀
    static final String KEY_CODE_PREFIX = "sms:code:phone:";

    @Resource
    private UserMapper userMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        //判断数据类型
        if (type==1){
            user.setUsername(data);
        }else if (type==2){
            user.setPhone(data);
        }else {
            throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(user)==0;
    }

    /**
     * 发送短信
     * 发送短信时，MQ回收到短信服务，ly-sms会调用短信服务
     * @param phone
     */
    public void sendCode(String phone) {
        amqpTemplate.convertAndSend("sms.verify.code",phone);
    }

    /**
     * 注册用户
     * @param user
     * @param code
     */
    public void register(User user, String code) {
        //1）校验短信验证码
        String key = KEY_CODE_PREFIX+user.getPhone();
        //根据由KEY_CODE_PREFIX和用户电话生成的键（key），在redis数据库中查找code（验证码）
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        //检验验证码
        if (!StringUtils.equals(code,cacheCode)) {
            throw  new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //2）生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //3）对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //4)注册并添加如数据库
        user.setCreated(new Date());
        int count = userMapper.insertSelective(user);
        if (count==0){
            throw new LyException(ExceptionEnum.BRAND__DELETE_ERROR);
        }
        //5）删除Redis中的验证码
        Boolean delete = stringRedisTemplate.delete(key);
        if (delete==false){
            throw new LyException(ExceptionEnum.BRAND__DELETE_CODE_ERROR);
        }
    }

    public User queryUser(String username, String password) {
        User u = new User();
        u.setUsername(username);
        User user = userMapper.selectOne(u);
        if (user==null) {
            //用户不存在
            //throw new LyException(ExceptionEnum.USER_USERNAME_NOTFOUND_ERROR);
            return null;
        }else if (!StringUtils.equals(CodecUtils.md5Hex(password,user.getSalt()),user.getPassword())){
            //密码错误
            //throw new LyException(ExceptionEnum.USER_PASSWORD_ERROR);
            return null;
        }
        return user;
    }
}
