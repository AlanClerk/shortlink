package com.nageoffer.shortlink.common.repository.admin.dto.req.group;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分组列表查询请求实体类
 */
@Data
public class GroupListReqDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
}
