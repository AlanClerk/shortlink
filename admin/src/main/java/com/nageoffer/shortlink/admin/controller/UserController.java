package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.common.repository.admin.dto.req.UserQueryReqDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.common.repository.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import com.nageoffer.shortlink.common.convention.result.Result;
import com.nageoffer.shortlink.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/v1")
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

}
