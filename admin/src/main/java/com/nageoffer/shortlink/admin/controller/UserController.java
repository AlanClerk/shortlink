package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.dto.req.UserQueryReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
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
    public UserRespDTO getUserByUsername(@RequestBody UserQueryReqDTO requestParam) {
        return userService.getUserByUsername(requestParam.getUsername());
    }
}
