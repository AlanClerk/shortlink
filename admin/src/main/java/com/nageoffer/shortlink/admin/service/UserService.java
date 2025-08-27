package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserLogoutReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;

/**
 * 用户接口层
 * 我的理解是这里不同于DAO，开始涉及到多表的、业务上的操作
 */
/// 注意这里要写interface，而不是class。因为这个接口继承了IService，IService是MyBatis-Plus提供的，如果写class就要实现一堆的接口了
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户响应实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 根据用户名查询用户真实信息（不脱敏）
     *
     * @param username 用户名
     * @return 用户真实响应实体
     */
    UserActualRespDTO getActualUserByUsername(String username);

    Boolean hasUserName(String username);

    /**
     * 用户注册
     *
     * @param requestParam 用户注册请求参数
     * @return 注册成功返回 true，失败返回 false
     */
    Boolean register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户信息
     *
     * @param requestParam 用户修改请求参数
     * @return 修改成功返回 true，失败返回 false
     */
    Boolean updateUser(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 用户登录请求参数
     * @return 登录成功返回token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 用户注销登录
     *
     * @param requestParam 用户登出请求参数
     * @return 注销成功返回 true，失败返回 false
     */
    Boolean logout(UserLogoutReqDTO requestParam);
}
