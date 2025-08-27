package com.nageoffer.shortlink.common.convention.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    /// A:来源于客户/客户端  B：来源于当前系统，为当前系统本身的问题 C：来源于第三方服务

    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),

    // ========== 二级宏观错误码 用户注册错误 ==========
    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    USER_NAME_VERIFY_ERROR("A000110", "用户名校验失败"),
    USER_NAME_EXIST_ERROR("A000111", "用户名已存在"),
    USER_NAME_SENSITIVE_ERROR("A000112", "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR("A000113", "用户名包含特殊字符"),
    USER_RECORD_EXIST_ERROR("A000114", "用户记录已存在"),
    USER_REGISTER_FREQUENTLY_ERROR("A000115", "用户注册过于频繁，请稍后重试"),
    USER_REGISTER_INTERRUPTED_ERROR("A000116", "用户注册被中断，请重试"),
    PASSWORD_VERIFY_ERROR("A000120", "密码校验失败"),
    PASSWORD_SHORT_ERROR("A000121", "密码长度不够"),
    PASSWORD_NOT_MATCH_ERROR("A000122", "两次密码输入不一致"),
    
    // ========== 二级宏观错误码 用户信息错误 ==========
    USER_NOT_EXIST_ERROR("A000130", "用户不存在"),
    USER_UPDATE_ERROR("A000131", "用户信息更新失败"),
    USER_PASSWORD_ERROR("A000132", "用户密码错误"),
    
    // ========== 二级宏观错误码 用户认证错误 ==========
    USER_TOKEN_INVALID_ERROR("A000140", "用户Token无效"),
    USER_TOKEN_EXPIRED_ERROR("A000141", "用户Token已过期"),
    USER_TOKEN_NOT_MATCH_ERROR("A000142", "Token与用户名不匹配"),
    
    PHONE_VERIFY_ERROR("A000151", "手机格式校验失败"),

    // ========== 二级宏观错误码 系统请求缺少幂等Token ==========
    IDEMPOTENT_TOKEN_NULL_ERROR("A000200", "幂等Token为空"),
    IDEMPOTENT_TOKEN_DELETE_ERROR("A000201", "幂等Token已被使用或失效"),

    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    // ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),

    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}