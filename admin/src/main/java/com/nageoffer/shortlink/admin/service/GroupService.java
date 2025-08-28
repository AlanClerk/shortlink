package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.common.repository.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupCreateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupListRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     *
     * @param requestParam 新增短链接分组请求参数
     * @return 分组标识
     */
    String createGroup(GroupCreateReqDTO requestParam);

    /**
     * 根据用户名查询分组列表
     *
     * @param username 用户名
     * @return 分组列表响应
     */
    List<GroupListRespDTO> listGroupsByUsername(String username);

    /**
     * 根据分组标识查询分组详情
     *
     * @param gid 分组标识
     * @return 分组详情响应
     */
    GroupRespDTO getGroupByGid(String gid);

    /**
     * 修改短链接分组
     *
     * @param requestParam 修改短链接分组请求参数
     * @return 修改成功返回 true，失败返回 false
     */
    Boolean updateGroup(GroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     *
     * @param gid 分组标识
     * @return 删除成功返回 true，失败返回 false
     */
    Boolean deleteGroup(String gid);

    /**
     * 排序短链接分组
     *
     * @param gid 分组标识
     * @param sortOrder 排序序号
     * @return 排序成功返回 true，失败返回 false
     */
    Boolean sortGroup(String gid, Integer sortOrder);

    /**
     * 检查分组标识是否存在
     *
     * @param gid 分组标识
     * @return 存在返回 true，不存在返回 false
     */
    Boolean hasGid(String gid);

    /**
     * 检查用户下分组名称是否存在
     *
     * @param username 用户名
     * @param name 分组名称
     * @return 存在返回 true，不存在返回 false
     */
    Boolean hasGroupName(String username, String name);
}
