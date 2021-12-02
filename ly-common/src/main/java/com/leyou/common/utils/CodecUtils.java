package com.leyou.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.UUID;

/**
 * 加密类
 */
public class CodecUtils {

    /**
     * MD5加密
     * @param data
     * @param salt
     * @return
     */
    public static String md5Hex(String data, String salt) {
        if (StringUtils.isBlank(salt)) {
            salt = data.hashCode() + "";
        }
        return DigestUtils.md5Hex(salt + DigestUtils.md5Hex(data));
    }

    /**
     * sha加密
     * @param data
     * @param salt
     * @return
     */
    public static String shaHex(String data, String salt) {
        if (StringUtils.isBlank(salt)) {
            salt = data.hashCode() + "";
        }
        return DigestUtils.sha512Hex(salt + DigestUtils.sha512Hex(data));
    }

    /**
     * 生成随机盐
     * @return
     */
    public static String generateSalt(){
        return StringUtils.replace(UUID.randomUUID().toString(),"-","");
    }

}
