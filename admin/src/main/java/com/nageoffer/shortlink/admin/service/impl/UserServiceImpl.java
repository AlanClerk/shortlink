package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nageoffer.shortlink.admin.service.UserService;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserLoginReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserLogoutReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserRegisterReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserLoginRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserRespDTO;

import java.util.UUID;

import com.nageoffer.shortlink.common.exception.ClientException;
import com.nageoffer.shortlink.common.constant.RedisCacheConstant;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserTokenRespDTO;
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
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        if (userDO == null) {
            throw new ClientException(USER_NOT_EXIST_ERROR);
        }

        // 验证密码（这里暂时是明文比较，实际项目中应该使用加密密码）
        if (!userDO.getPassword().equals(requestParam.getPassword())) {
            throw new ClientException(USER_PASSWORD_ERROR);
        }

        // 将用户信息和token存储到Redis - 改为String结构
        String loginKey = RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername();
        try {
            // 检查是否已存在登录信息
            Object existingTokenObj = redisTemplate.opsForValue().get(loginKey);
            String existingTokenJson = existingTokenObj != null ? existingTokenObj.toString() : null;
            
            if (existingTokenJson != null) {
                // 如果已经存在登录信息，返回旧的token并刷新过期时间
                UserTokenRespDTO existingUserToken = objectMapper.readValue(existingTokenJson, UserTokenRespDTO.class);
                // 刷新过期时间
                redisTemplate.expire(loginKey, 30, TimeUnit.MINUTES);
                return UserLoginRespDTO.builder()
                        .token(existingUserToken.getToken())
                        .build();
            }

            // 如果不存在登录信息，生成新的token并存储
            String token = UUID.randomUUID().toString();
            
            // 创建用户DTO用于存储到Redis
            UserTokenRespDTO userTokenDTO = new UserTokenRespDTO();
            BeanUtils.copyProperties(userDO, userTokenDTO);
            userTokenDTO.setToken(token);
            
            // 使用String结构存储，同时设置30分钟过期时间
            String userDTOJson = objectMapper.writeValueAsString(userTokenDTO);
            redisTemplate.opsForValue().set(loginKey, userDTOJson, 30, TimeUnit.MINUTES);
            
            return UserLoginRespDTO.builder()
                    .token(token)
                    .build();
        } catch (JsonProcessingException e) {
            throw new ClientException(USER_LOGIN_JSON_ERROR);
        } catch (Exception e) {
            throw new ClientException(LOGIN_ERROR);
        }
    }

    @Override
    public Boolean logout(UserLogoutReqDTO requestParam) {
        // 构造Redis key
        String loginKey = RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername();

        try {
            // 获取Redis中存储的用户登录信息
            Object storedTokenObj = redisTemplate.opsForValue().get(loginKey);
            String storedTokenJson = storedTokenObj != null ? storedTokenObj.toString() : null;
            
            if (storedTokenJson == null) {
                // 如果Redis中不存在该用户信息，直接返回true（已经是登出状态）
                return true;
            }

            // 解析存储的用户信息并验证token是否匹配
            UserTokenRespDTO storedUserToken = objectMapper.readValue(storedTokenJson, UserTokenRespDTO.class);
            if (!storedUserToken.getToken().equals(requestParam.getToken())) {
                throw new ClientException("Token不匹配或已失效");
            }

            // 从Redis中删除用户登录信息
            Boolean deleteResult = redisTemplate.delete(loginKey);
            return deleteResult != null && deleteResult;
        } catch (JsonProcessingException e) {
            // JSON解析失败
            throw new ClientException(USER_LOGOUT_JSON_ERROR);
        } catch (ClientException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 其他异常
            throw new ClientException(LOGOUT_ERROR);
        }
    }
}
