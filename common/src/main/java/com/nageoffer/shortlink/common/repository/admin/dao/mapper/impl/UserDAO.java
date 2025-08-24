package com.nageoffer.shortlink.common.repository.admin.dao.mapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问对象
 * 封装用户相关的复杂数据库操作
 */
@Repository
@RequiredArgsConstructor
public class UserDAO {
    // 使用构造器方式注入，需要@RequiredArgsConstructor注解
    private final UserMapper userMapper;

    /**
     * 根据用户名查询用户信息
     */
    public UserDO selectOneByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username)
                   .eq(UserDO::getDelFlag, 0); // 只查询未删除的用户
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户名查询用户是否存在
     */
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 根据手机号查询用户信息
     */
    public UserDO selectOneByPhone(String phone) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getPhone, phone)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邮箱查询用户信息
     */
    public UserDO selectOneByMail(String mail) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getMail, mail)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 插入用户
     */
    public boolean insertUser(UserDO userDO) {
        userDO.setCreateTime(LocalDateTime.now());
        userDO.setUpdateTime(LocalDateTime.now());
        userDO.setDelFlag(0);
        return userMapper.insert(userDO) > 0;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(UserDO userDO) {
        userDO.setUpdateTime(LocalDateTime.now());
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDO::getId, userDO.getId())
                    .eq(UserDO::getDelFlag, 0);
        return userMapper.update(userDO, updateWrapper) > 0;
    }

    /**
     * 根据ID逻辑删除用户
     */
    public boolean deleteUserById(Long id) {
        UserDO userDO = new UserDO();
        userDO.setDelFlag(1);
        userDO.setDeletionTime(System.currentTimeMillis());
        userDO.setUpdateTime(LocalDateTime.now());
        
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDO::getId, id)
                    .eq(UserDO::getDelFlag, 0);
        return userMapper.update(userDO, updateWrapper) > 0;
    }

    /**
     * 根据用户名逻辑删除用户
     */
    public boolean deleteUserByUsername(String username) {
        UserDO userDO = new UserDO();
        userDO.setDelFlag(1);
        userDO.setDeletionTime(System.currentTimeMillis());
        userDO.setUpdateTime(LocalDateTime.now());
        
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDO::getUsername, username)
                    .eq(UserDO::getDelFlag, 0);
        return userMapper.update(userDO, updateWrapper) > 0;
    }

    /**
     * 根据ID查询用户（不包含已删除）
     */
    public UserDO selectUserById(Long id) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getId, id)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 分页查询用户列表
     */
    public List<UserDO> selectUserList(int offset, int limit) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getDelFlag, 0)
                   .orderByDesc(UserDO::getCreateTime)
                   .last("LIMIT " + offset + ", " + limit);
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 统计用户总数（不包含已删除）
     */
    public long countUsers() {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getDelFlag, 0);
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 更新用户密码
     */
    public boolean updatePassword(Long id, String newPassword) {
        UserDO userDO = new UserDO();
        userDO.setPassword(newPassword);
        userDO.setUpdateTime(LocalDateTime.now());
        
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDO::getId, id)
                    .eq(UserDO::getDelFlag, 0);
        return userMapper.update(userDO, updateWrapper) > 0;
    }

    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getPhone, phone)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查邮箱是否已存在
     */
    public boolean existsByMail(String mail) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getMail, mail)
                   .eq(UserDO::getDelFlag, 0);
        return userMapper.selectCount(queryWrapper) > 0;
    }
}
