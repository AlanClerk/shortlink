package com.nageoffer.shortlink.common.repository.admin.dto.req.group;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分组排序请求实体类
 */
@Data
public class GroupSortReqDTO {

    /**
     * 分组标识
     */
    @NotBlank(message = "分组标识不能为空")
    private String gid;

    /**
     * 排序序号
     */
    @NotNull(message = "排序序号不能为空")
    private Integer sortOrder;
}
