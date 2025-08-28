package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupCreateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.group.GroupUpdateReqDTO;
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
    @PostMapping
    public Result<String> createGroup(@RequestBody @Valid GroupCreateReqDTO requestParam) {
        String gid = groupService.createGroup(requestParam);
        return Results.success(gid);
    }

    /**
     * 查询短链接分组列表
     */
    @GetMapping
    public Result<List<GroupListRespDTO>> listGroups(@RequestParam String username) {
        List<GroupListRespDTO> result = groupService.listGroupsByUsername(username);
        return Results.success(result);
    }

    /**
     * 根据分组标识查询短链接分组详情
     */
    @GetMapping("/{gid}")
    public Result<GroupRespDTO> getGroup(@PathVariable String gid) {
        GroupRespDTO result = groupService.getGroupByGid(gid);
        return Results.success(result);
    }

    /**
     * 修改短链接分组
     */
    @PutMapping
    public Result<Void> updateGroup(@RequestBody @Valid GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/{gid}")
    public Result<Void> deleteGroup(@PathVariable String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 排序短链接分组
     */
    @PutMapping("/sort")
    public Result<Void> sortGroup(@RequestParam String gid, @RequestParam Integer sortOrder) {
        groupService.sortGroup(gid, sortOrder);
        return Results.success();
    }

    /**
     * 检查分组标识是否存在
     */
    @GetMapping("/has-gid")
    public Result<Boolean> hasGid(@RequestParam String gid) {
        Boolean result = groupService.hasGid(gid);
        return Results.success(result);
    }

    /**
     * 检查用户下分组名称是否存在
     */
    @GetMapping("/has-group-name")
    public Result<Boolean> hasGroupName(@RequestParam String username, @RequestParam String name) {
        Boolean result = groupService.hasGroupName(username, name);
        return Results.success(result);
    }
}
