package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.common.repository.admin.dao.mapper.impl.GroupDAO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupCreateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupListRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupRespDTO;
import com.nageoffer.shortlink.common.exception.ClientException;
import com.nageoffer.shortlink.common.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 短链接分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final GroupDAO groupDAO;
    private final RedissonClient redissonClient;

    @Override
    public String createGroup(GroupCreateReqDTO requestParam) {
        String username = getCurrentUsername();
        
        // 检查分组名称是否已存在
        if (groupDAO.existsByUsernameAndName(username, requestParam.getName())) {
            throw new ClientException("分组名称已存在");
        }

        // 生成分组标识
        String gid = generateGid();
        
        // 分布式锁防止并发创建相同gid
        RLock lock = redissonClient.getLock("create_group_" + gid);
        lock.lock();
        try {
            // 再次检查gid是否存在
            while (groupDAO.existsByGid(gid)) {
                gid = generateGid();
            }

            GroupDO groupDO = new GroupDO();
            groupDO.setGid(gid);
            groupDO.setName(requestParam.getName());
            groupDO.setUsername(username);
            groupDO.setSortOrder(requestParam.getSortOrder() != null ? 
                requestParam.getSortOrder() : groupDAO.getNextSortOrder(username));
            groupDO.setCreateTime(LocalDateTime.now());
            groupDO.setUpdateTime(LocalDateTime.now());
            groupDO.setDelFlag(0);

            save(groupDO);
            return gid;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<GroupListRespDTO> listGroupsByUsername(String username) {
        List<GroupDO> groupList = groupDAO.selectListByUsername(username);
        return BeanUtil.convert(groupList, GroupListRespDTO.class);
    }

    @Override
    public GroupRespDTO getGroupByGid(String gid) {
        String username = getCurrentUsername();
        GroupDO groupDO = groupDAO.selectOneByUsernameAndGid(username, gid);
        if (groupDO == null) {
            throw new ClientException("分组不存在");
        }
        return BeanUtil.convert(groupDO, GroupRespDTO.class);
    }

    @Override
    public Boolean updateGroup(GroupUpdateReqDTO requestParam) {
        String username = getCurrentUsername();
        
        // 检查分组是否存在
        GroupDO existGroup = groupDAO.selectOneByUsernameAndGid(username, requestParam.getGid());
        if (existGroup == null) {
            throw new ClientException("分组不存在");
        }

        // 如果修改分组名称，检查是否与其他分组重名
        if (requestParam.getName() != null && 
            !requestParam.getName().equals(existGroup.getName()) &&
            groupDAO.existsByUsernameAndName(username, requestParam.getName())) {
            throw new ClientException("分组名称已存在");
        }

        // 更新分组信息
        if (requestParam.getName() != null) {
            groupDAO.updateName(requestParam.getGid(), username, requestParam.getName());
        }
        
        if (requestParam.getSortOrder() != null) {
            groupDAO.updateSortOrder(requestParam.getGid(), username, requestParam.getSortOrder());
        }

        return true;
    }

    @Override
    public Boolean deleteGroup(String gid) {
        String username = getCurrentUsername();
        
        // 检查分组是否存在
        GroupDO existGroup = groupDAO.selectOneByUsernameAndGid(username, gid);
        if (existGroup == null) {
            throw new ClientException("分组不存在");
        }

        // TODO: 检查分组下是否有短链接，如果有则不允许删除
        
        return groupDAO.logicDelete(gid, username) > 0;
    }

    @Override
    public Boolean sortGroup(String gid, Integer sortOrder) {
        String username = getCurrentUsername();
        
        // 检查分组是否存在
        GroupDO existGroup = groupDAO.selectOneByUsernameAndGid(username, gid);
        if (existGroup == null) {
            throw new ClientException("分组不存在");
        }

        return groupDAO.updateSortOrder(gid, username, sortOrder) > 0;
    }

    @Override
    public Boolean hasGid(String gid) {
        return groupDAO.existsByGid(gid);
    }

    @Override
    public Boolean hasGroupName(String username, String name) {
        return groupDAO.existsByUsernameAndName(username, name);
    }

    /**
     * 生成分组标识
     */
    private String generateGid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    /**
     * 获取当前用户名（此处为示例，实际应从上下文中获取）
     */
    private String getCurrentUsername() {
        // TODO: 从SecurityContext或请求头中获取当前用户名
        return "admin"; // 临时返回，实际需要从认证上下文获取
    }
}
