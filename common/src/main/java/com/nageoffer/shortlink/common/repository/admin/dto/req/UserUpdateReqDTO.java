package com.nageoffer.shortlink.common.repository.admin.dto.req;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户修改请求实体类
 */
@Data
public class UserUpdateReqDTO {
    
    /**
     * 用户名（用于查找要修改的用户）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 新密码（可选）
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 真实姓名（可选）
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    /**
     * 手机号（可选）
     */
    private String phone;
    
    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String mail;
}
