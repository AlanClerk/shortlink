package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageoffer.shortlink.admin.service.UserService;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserLogoutReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserRespDTO;
import java.util.UUID;
import com.nageoffer.shortlink.common.exception.ClientException;
import com.nageoffer.shortlink.common.constant.RedisCacheConstant;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.common.convention.errorcode.BaseErrorCode.*;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    /// 布隆过滤器，十分地有用
    private final RBloomFilter<String> rBloomFilter;

    private final RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ObjectMapper objectMapper;

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

    @Override
    public Boolean register(UserRegisterReqDTO requestParam) {
        // 验证两次密码是否一致
        if (!requestParam.getPassword().equals(requestParam.getConfirmPassword())) {
            throw new ClientException(PASSWORD_NOT_MATCH_ERROR);
        }
        
        // 检查用户名是否已存在（第一次检查，快速失败）
        if (hasUserName(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }

        // 获取分布式锁
        String lockKey = RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，等待时间5秒，锁自动释放时间30秒
            if (lock.tryLock(5, 30, SECONDS)) {
                try {
                    // 在锁内再次检查用户名是否已存在（双重检查）
                    if (hasUserName(requestParam.getUsername())) {
                        throw new ClientException(USER_NAME_EXIST_ERROR);
                    }
                    
                    // 创建用户实体
                    UserDO userDO = new UserDO();
                    BeanUtils.copyProperties(requestParam, userDO);
                    
//                    // 加密密码
//                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//                    userDO.setPassword(passwordEncoder.encode(requestParam.getPassword()));
//                    加密密码稍后再用序列化的方式进行加密

                    // 设置创建时间和更新时间
                    LocalDateTime now = LocalDateTime.now();
                    userDO.setCreateTime(now);
                    userDO.setUpdateTime(now);
                    userDO.setDelFlag(0);
                    
                    // 保存用户到数据库
                    int insertResult = baseMapper.insert(userDO);
                    
                    // 将用户名加入布隆过滤器
                    if (insertResult > 0) {
                        rBloomFilter.add(requestParam.getUsername());
                        return true;
                    } else {
                        throw new ClientException(USER_REGISTER_ERROR);
                    }
                } finally {
                    // 确保释放锁
                    lock.unlock();
                }
            } else {
                // 获取锁失败
                throw new ClientException(USER_REGISTER_FREQUENTLY_ERROR);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClientException(USER_REGISTER_INTERRUPTED_ERROR);
        }
    }

    @Override
    public Boolean updateUser(UserUpdateReqDTO requestParam) {
        // 查找要修改的用户
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO existingUser = baseMapper.selectOne(queryWrapper);
        
        if (existingUser == null) {
            throw new ClientException(USER_NOT_EXIST_ERROR);
        }
        
        // 准备更新的用户实体
        UserDO updateUser = new UserDO();
        updateUser.setId(existingUser.getId());
        
        // 只更新非空字段
        if (requestParam.getPassword() != null && !requestParam.getPassword().trim().isEmpty()) {
            // 这里可以添加密码加密逻辑
            // BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // updateUser.setPassword(passwordEncoder.encode(requestParam.getPassword()));
            updateUser.setPassword(requestParam.getPassword());
        }
        
        if (requestParam.getRealName() != null && !requestParam.getRealName().trim().isEmpty()) {
            updateUser.setRealName(requestParam.getRealName());
        }
        
        if (requestParam.getPhone() != null && !requestParam.getPhone().trim().isEmpty()) {
            updateUser.setPhone(requestParam.getPhone());
        }
        
        if (requestParam.getMail() != null && !requestParam.getMail().trim().isEmpty()) {
            updateUser.setMail(requestParam.getMail());
        }
        
        // 设置更新时间
        updateUser.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int updateResult = baseMapper.updateById(updateUser);
        
        if (updateResult > 0) {
            return true;
        } else {
            throw new ClientException(USER_UPDATE_ERROR);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 根据用户名查询用户信息
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        
        if (userDO == null) {
            throw new ClientException(USER_NOT_EXIST_ERROR);
        }
        
        // 验证密码（这里暂时是明文比较，实际项目中应该使用加密密码）
        if (!userDO.getPassword().equals(requestParam.getPassword())) {
            throw new ClientException(USER_PASSWORD_ERROR);
        }
        
        // 生成UUID token
        String token = UUID.randomUUID().toString();
        
        // 创建用户DTO用于存储到Redis
        UserRespDTO userDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userDTO);
        
        // 将用户信息和token存储到Redis
        String loginKey = RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername();
        try {
            String userDtoJson = objectMapper.writeValueAsString(userDTO);
            // TODO 修改成一个新的包括token的DTO
//            redisTemplate.opsForHash().put(loginKey, "userInfo", userDtoJson);
            redisTemplate.opsForHash().put(loginKey, "token", token);
            // 设置30分钟过期时间
            redisTemplate.expire(loginKey, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new ClientException(SERVICE_ERROR);
        }
        
        return UserLoginRespDTO.builder()
                .token(token)
                .build();
    }

    @Override
    public Boolean logout(UserLogoutReqDTO requestParam) {
        // 构造Redis key
        String loginKey = RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername();
        
        // 先检查Redis中是否存在该用户的登录信息
        Boolean hasKey = redisTemplate.hasKey(loginKey);
        if (!hasKey) {
            // 如果Redis中不存在该用户信息，直接返回true（已经是登出状态）
            return true;
        }
        
        // 验证token是否匹配
        Object storedToken = redisTemplate.opsForHash().get(loginKey, "token");
        if (storedToken == null || !storedToken.toString().equals(requestParam.getToken())) {
            throw new ClientException("Token不匹配或已失效");
        }
        
        // 从Redis中删除用户登录信息
        Boolean deleteResult = redisTemplate.delete(loginKey);
        return deleteResult != null && deleteResult;
    }
}
