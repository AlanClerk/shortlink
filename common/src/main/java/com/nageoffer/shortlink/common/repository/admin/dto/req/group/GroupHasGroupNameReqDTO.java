package com.nageoffer.shortlink.common.repository.admin.dto.req.group;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 检查分组名称是否存在请求实体类
 */
@Data
public class GroupHasGroupNameReqDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    private String name;
}
