package com.nageoffer.shortlink.common.repository.admin.dto.resp.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 不含序列化的用户响应实体类
 */
@Data
public class UserActualRespDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
