package com.nageoffer.shortlink.common.repository.admin.dto.resp.group;

import lombok.Data;

/**
 * 分组列表响应实体类
 */
@Data
public class GroupListRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 分组内短链接数量
     */
    private Integer linkCount;
    ///  分组内短链接数量暂时不知道
}
