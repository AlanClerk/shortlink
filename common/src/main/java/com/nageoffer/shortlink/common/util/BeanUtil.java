package com.nageoffer.shortlink.common.util;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对象属性复制工具类
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BeanUtil {

    /**
     * 属性复制
     *
     * @param source 数据对象
     * @param target 目标对象
     * @param <T>
     * @param <S>
     * @return 转换后对象
     */
    public static <T, S> T convert(S source, T target) {
        if (source != null) {
            BeanUtils.copyProperties(source, target);
        }
        return target;
    }

    /**
     * 复制单个对象
     *
     * @param source 数据对象
     * @param clazz  复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象
     */
    public static <T, S> T convert(S source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean conversion failed", e);
        }
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    public static <T, S> List<T> convert(List<S> sources, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        }
        return sources.stream()
                .map(source -> convert(source, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    public static <T, S> Set<T> convert(Set<S> sources, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        }
        return sources.stream()
                .map(source -> convert(source, clazz))
                .collect(Collectors.toSet());
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    @SuppressWarnings("unchecked")
    public static <T, S> T[] convert(S[] sources, Class<T> clazz) {
        if (sources == null || sources.length == 0) {
            return null;
        }
        T[] targetArray = (T[]) Array.newInstance(clazz, sources.length);
        for (int i = 0; i < sources.length; i++) {
            targetArray[i] = convert(sources[i], clazz);
        }
        return targetArray;
    }

    /**
     * 拷贝非空且非空串属性
     * 注意：Spring BeanUtils 不支持条件复制，需要自定义实现
     *
     * @param source 数据源
     * @param target 指向源
     */
    public static void convertIgnoreNullAndBlank(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        // Spring BeanUtils 会自动忽略 null 值
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 拷贝非空属性
     *
     * @param source 数据源
     * @param target 指向源
     */
    public static void convertIgnoreNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        // Spring BeanUtils 会自动忽略 null 值
        BeanUtils.copyProperties(source, target);
    }
}