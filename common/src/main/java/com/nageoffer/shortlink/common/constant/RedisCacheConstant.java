package com.nageoffer.shortlink.common.constant;

/**
 * Redis缓存常量
 */
public class RedisCacheConstant {

    /**
     * 用户注册分布式锁Key
     */
    public static final String LOCK_USER_REGISTER_KEY = "shortlink:lock_user-register:";
    
    /**
     * 用户登录缓存Key前缀
     */
    public static final String USER_LOGIN_KEY = "login_";
}
