package com.nageoffer.shortlink.common.util.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 手机号脱敏序列化器
 */
public class PhoneDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String phoneDesensitization = desensitizeMobilePhone(phone);
        jsonGenerator.writeString(phoneDesensitization);
    }

    /**
     * 手机号脱敏处理
     * 完全按照hutool的逻辑实现：StrUtil.isBlank(num) ? "" : StrUtil.hide(num, 3, num.length() - 4)
     * 保留前3位和后4位，中间用*号替换
     * 例如：13812345678 -> 138****5678
     *
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    private String desensitizeMobilePhone(String phone) {
        return isBlank(phone) ? "" : hide(phone, 3, phone.length() - 4);
    }

    /**
     * 判断字符串是否为空白
     * 对应hutool的StrUtil.isBlank方法
     *
     * @param str 字符串
     * @return 是否为空白
     */
    private boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 隐藏字符串的指定部分，用*替换
     * 对应hutool的StrUtil.hide方法
     *
     * @param str        原始字符串
     * @param startIndex 开始位置（包含）
     * @param endIndex   结束位置（不包含）
     * @return 处理后的字符串
     */
    private String hide(String str, int startIndex, int endIndex) {
        if (str == null || str.length() == 0) {
            return str;
        }
        
        // 确保索引在有效范围内
        startIndex = Math.max(0, startIndex);
        endIndex = Math.min(str.length(), endIndex);
        
        if (startIndex >= endIndex) {
            return str;
        }
        
        StringBuilder sb = new StringBuilder(str);
        for (int i = startIndex; i < endIndex; i++) {
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }
}