package com.nageoffer.shortlink.common.repository.admin.dto.req.user;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登出请求实体类
 */
@Data
public class UserLogoutReqDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 访问令牌
     */
    @NotBlank(message = "访问令牌不能为空")
    private String token;
}
