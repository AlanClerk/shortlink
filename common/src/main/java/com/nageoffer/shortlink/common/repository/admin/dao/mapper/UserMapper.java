package com.nageoffer.shortlink.common.repository.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 用户持久层
 * 我的理解是单表操作就在这里完成
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 根据用户名查询用户信息
     */
    UserDO selectOneByUsername(@Param("username") String username);
}
