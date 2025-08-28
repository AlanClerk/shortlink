package com.nageoffer.shortlink.common.repository.admin.dto.req.group;

import lombok.Data;

/**
 * 分组查询请求实体类
 */
@Data
public class GroupQueryReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
}
