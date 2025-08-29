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
import com.nageoffer.shortlink.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.common.exception.ClientException;
import com.nageoffer.shortlink.common.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 短链接分组接口实现层
 */
@Slf4j
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
            throw new ClientException(BaseErrorCode.GROUP_NAME_EXIST_ERROR);
        }
        
        // 生成唯一的gid
        String gid;
        do {
            gid = generateGid();
        } while (groupDAO.existsByGid(gid));
        
        // 使用builder模式构建GroupDO对象
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(requestParam.getName())
                .username(username)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .delFlag(0)  // 0表示未删除，1表示已删除
                .build();
        baseMapper.insert(groupDO);
        
        return gid;
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
            throw new ClientException(BaseErrorCode.GROUP_NOT_EXIST_ERROR);
        }
        return BeanUtil.convert(groupDO, GroupRespDTO.class);
    }

    @Override
    public Boolean updateGroup(GroupUpdateReqDTO requestParam) {
        String username = getCurrentUsername();
        
        // 检查分组是否存在
        GroupDO existGroup = groupDAO.selectOneByUsernameAndGid(username, requestParam.getGid());
        if (existGroup == null) {
            throw new ClientException(BaseErrorCode.GROUP_NOT_EXIST_ERROR);
        }

        // 如果修改分组名称，检查是否与其他分组重名
        if (requestParam.getName() != null && 
            !requestParam.getName().equals(existGroup.getName()) &&
            groupDAO.existsByUsernameAndName(username, requestParam.getName())) {
            throw new ClientException(BaseErrorCode.GROUP_NAME_EXIST_ERROR);
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
            throw new ClientException(BaseErrorCode.GROUP_NOT_EXIST_ERROR);
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
            throw new ClientException(BaseErrorCode.GROUP_NOT_EXIST_ERROR);
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
     * 生成分组标识 - 6位数字字母混合字符串
     */
    private String generateGid() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder gid = new StringBuilder(6);
        
        for (int i = 0; i < 6; i++) {
            gid.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return gid.toString();
    }

    /**
     * 获取当前用户名（此处为示例，实际应从上下文中获取）
     */
    private String getCurrentUsername() {
        // TODO: 从SecurityContext或请求头中获取当前用户名，这部分应该是要由网关来存到ThreadLocal的，这里只是为了演示，后续再进行补充
        return "admin"; // 临时返回，实际需要从认证上下文获取
    }
}
