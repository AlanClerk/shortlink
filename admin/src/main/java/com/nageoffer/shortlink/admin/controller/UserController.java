package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserLoginReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserLogoutReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserQueryReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserRegisterReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.req.user.UserUpdateReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserLoginRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.user.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import com.nageoffer.shortlink.common.convention.result.Result;
import com.nageoffer.shortlink.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/short-link/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     *
     * @param requestParam 用户查询请求参数
     * @return 用户响应信息
     */
    @PostMapping("/getUserByUsername")
    public Result<UserRespDTO> getUserByUsername(@RequestBody UserQueryReqDTO requestParam) {
        return Results.success(userService.getUserByUsername(requestParam.getUsername()));
    }

    /**
     * 根据用户名查询用户真实信息（不脱敏）
     *
     * @param requestParam 用户查询请求参数
     * @return 用户真实响应信息
     */
    @PostMapping("/actual/getUserByUsername")
    public Result<UserActualRespDTO> getActualUserByUsername(@RequestBody UserQueryReqDTO requestParam) {
        return Results.success(userService.getActualUserByUsername(requestParam.getUsername()));
    }

    /**
     * 查询用户名是否可用
     *
     * @param requestParam 用户查询请求参数
     * @return 用户名是否可用
     */
    @PostMapping("/hasUsername")
    public Result<Boolean> hasUsername(@RequestBody UserQueryReqDTO requestParam) {
        return Results.success(!userService.hasUserName(requestParam.getUsername()));
    }

    /**
     * 用户注册
     *
     * @param requestParam 用户注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 根据用户名修改用户信息
     *
     * @param requestParam 用户修改请求参数
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody @Valid UserUpdateReqDTO requestParam) {
        userService.updateUser(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     *
     * @param requestParam 用户登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody @Valid UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 用户注销登录
     *
     * @param requestParam 用户登出请求参数（包含用户名和token）
     * @return 注销结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody @Valid UserLogoutReqDTO requestParam) {
        userService.logout(requestParam);
        return Results.success();
    }

}
