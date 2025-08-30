package com.nageoffer.shortlink.common.repository.admin.dao.mapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短链接分组数据访问对象
 * 封装分组相关的复杂数据库操作
 */
@Repository
@RequiredArgsConstructor
public class GroupDAO {
    
    private final GroupMapper groupMapper;

    /**
     * 根据分组标识查询分组信息
     */
    public GroupDO selectOneByGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getGid, gid)
                   .eq(GroupDO::getDelFlag, 0); // 只查询未删除的分组
        return groupMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户名查询分组列表
     */
    @SuppressWarnings("unchecked")
    public List<GroupDO> selectListByUsername(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                   .eq(GroupDO::getDelFlag, 0)
                   .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime); // 按排序和创建时间排序
        return groupMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户名和分组标识查询分组信息
     */
    public GroupDO selectOneByUsernameAndGid(String username, String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                   .eq(GroupDO::getGid, gid)
                   .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectOne(queryWrapper);
    }

    /**
     * 检查分组标识是否存在
     */
    public boolean existsByGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getGid, gid)
                   .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查用户下分组名称是否存在
     */
    public boolean existsByUsernameAndName(String username, String name) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                   .eq(GroupDO::getName, name)
                   .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 逻辑删除分组
     */
    public int logicDelete(String gid, String username) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0)
                    .set(GroupDO::getDelFlag, 1)
                    .set(GroupDO::getUpdateTime, LocalDateTime.now());
        return groupMapper.update(null, updateWrapper);
    }

    /**
     * 更新分组排序
     */
    public int updateSortOrder(String gid, String username, Integer sortOrder) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0)
                    .set(GroupDO::getSortOrder, sortOrder)
                    .set(GroupDO::getUpdateTime, LocalDateTime.now());
        return groupMapper.update(null, updateWrapper);
    }

    /**
     * 更新分组名称
     */
    public int updateName(String gid, String username, String name) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupDO::getGid, gid)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0)
                    .set(GroupDO::getName, name)
                    .set(GroupDO::getUpdateTime, LocalDateTime.now());
        return groupMapper.update(null, updateWrapper);
    }

    /**
     * 获取用户分组数量
     */
    public long countByUsername(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                   .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectCount(queryWrapper);
    }

    /**
     * 获取用户下一个可用的排序号
     */
    public Integer getNextSortOrder(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                   .eq(GroupDO::getDelFlag, 0)
                   .orderByDesc(GroupDO::getSortOrder)
                   .last("LIMIT 1");
        GroupDO lastGroup = groupMapper.selectOne(queryWrapper);
        return lastGroup != null && lastGroup.getSortOrder() != null ? 
                lastGroup.getSortOrder() + 1 : 1;
    }
}
