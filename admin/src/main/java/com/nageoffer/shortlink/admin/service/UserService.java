package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
}
