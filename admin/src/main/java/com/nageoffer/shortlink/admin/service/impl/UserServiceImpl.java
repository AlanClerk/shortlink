package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.service.UserService;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserRespDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    /// 布隆过滤器，十分地有用
    private final RBloomFilter<String> rBloomFilter;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        
        if (userDO == null) {
            return null;
        }
        
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    @Override
    public UserActualRespDTO getActualUserByUsername(String username) {
        UserRespDTO userRespDTO = getUserByUsername(username);
        
        if (userRespDTO == null) {
            return null;
        }
        
        UserActualRespDTO userActualRespDTO = new UserActualRespDTO();
        BeanUtils.copyProperties(userRespDTO, userActualRespDTO);
        return userActualRespDTO;
    }

    @Override
    public Boolean hasUserName(String username) {
        return rBloomFilter.contains(username);
    }
}
