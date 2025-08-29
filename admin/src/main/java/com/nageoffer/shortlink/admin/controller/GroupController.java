package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.*;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupListRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.group.GroupRespDTO;
import com.nageoffer.shortlink.common.convention.result.Result;
import com.nageoffer.shortlink.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 短链接分组控制层
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/group")
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/create")
    public Result<String> createGroup(@RequestBody @Valid GroupCreateReqDTO requestParam) {
        String gid = groupService.createGroup(requestParam);
        return Results.success(gid);
    }

    /**
     * 查询短链接分组列表
     */
    @PostMapping("/list")
    public Result<List<GroupListRespDTO>> listGroups(@RequestBody @Valid GroupListReqDTO requestParam) {
        List<GroupListRespDTO> result = groupService.listGroupsByUsername(requestParam.getUsername());
        return Results.success(result);
    }

    /**
     * 根据分组标识查询短链接分组详情
     */
    @PostMapping("/get")
    public Result<GroupRespDTO> getGroup(@RequestBody @Valid GroupGetReqDTO requestParam) {
        GroupRespDTO result = groupService.getGroupByGid(requestParam.getGid());
        return Results.success(result);
    }

    /**
     * 修改短链接分组
     */
    @PostMapping("/update")
    public Result<Void> updateGroup(@RequestBody @Valid GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @PostMapping("/delete")
    public Result<Void> deleteGroup(@RequestBody @Valid GroupDeleteReqDTO requestParam) {
        groupService.deleteGroup(requestParam.getGid());
        return Results.success();
    }

    /**
     * 排序短链接分组
     */
    @PostMapping("/sort")
    public Result<Void> sortGroup(@RequestBody @Valid GroupSortReqDTO requestParam) {
        groupService.sortGroup(requestParam.getGid(), requestParam.getSortOrder());
        return Results.success();
    }

    /**
     * 检查分组标识是否存在
     */
    @PostMapping("/has-gid")
    public Result<Boolean> hasGid(@RequestBody @Valid GroupHasGidReqDTO requestParam) {
        Boolean result = groupService.hasGid(requestParam.getGid());
        return Results.success(result);
    }

    /**
     * 检查用户下分组名称是否存在
     */
    @PostMapping("/has-group-name")
    public Result<Boolean> hasGroupName(@RequestBody @Valid GroupHasGroupNameReqDTO requestParam) {
        Boolean result = groupService.hasGroupName(requestParam.getUsername(), requestParam.getName());
        return Results.success(result);
    }
}
