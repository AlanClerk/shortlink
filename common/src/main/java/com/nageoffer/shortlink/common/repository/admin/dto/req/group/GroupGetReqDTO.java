package com.nageoffer.shortlink.common.repository.admin.dto.req.group;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分组查询详情请求实体类
 */
@Data
public class GroupGetReqDTO {

    /**
     * 分组标识
     */
    @NotBlank(message = "分组标识不能为空")
    private String gid;
}
