package com.nageoffer.shortlink.common.repository.admin.dto.resp.group;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分组响应实体类
 */
@Data
public class GroupRespDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
