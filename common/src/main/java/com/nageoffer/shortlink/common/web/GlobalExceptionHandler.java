package com.nageoffer.shortlink.common.web;

import com.nageoffer.shortlink.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.common.convention.result.Result;
import com.nageoffer.shortlink.common.convention.result.Results;
import com.nageoffer.shortlink.common.exception.AbstractException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 * 
 * <h3>重构历史与经验教训：</h3>
 * <h4>1. 依赖优化经验 (2024重构)</h4>
 * <ul>
 *   <li><b>问题：</b>原代码依赖hutool工具库，增加了项目的外部依赖复杂度</li>
 *   <li><b>解决：</b>使用Java标准库替换hutool依赖</li>
 *   <li><b>具体改进：</b>
 *     <ul>
 *       <li>CollectionUtil.getFirst() → bindingResult.getFieldErrors().stream().findFirst().orElse(null)</li>
 *       <li>StrUtil.EMPTY → ""</li>
 *       <li>StringUtils.isEmpty() → !StringUtils.hasText() (解决deprecation警告)</li>
 *     </ul>
 *   </li>
 *   <li><b>收益：</b>减少外部依赖，提高代码标准化程度，使用现代Java Stream API</li>
 * </ul>
 * 
 * <h4>2. 泛型类型安全经验</h4>
 * <ul>
 *   <li><b>问题：</b>"形参化类 'Result' 的原始使用" 警告</li>
 *   <li><b>原因：</b>Result&lt;T&gt; 是泛型类，使用时必须指定类型参数，否则失去类型安全性</li>
 *   <li><b>解决：</b>统一使用 Result&lt;Void&gt; 作为异常处理方法的返回类型</li>
 *   <li><b>教训：</b>
 *     <ul>
 *       <li>泛型不仅是语法要求，更是类型安全的保障</li>
 *       <li>原始类型使用会导致编译时类型检查失效，增加运行时ClassCastException风险</li>
 *       <li>异常处理方法通常不返回业务数据，使用 Void 类型最合适</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h4>3. 依赖管理最佳实践</h4>
 * <ul>
 *   <li><b>原则：</b>优先使用JDK标准库 > Spring生态 > 第三方工具库</li>
 *   <li><b>添加的Spring依赖：</b>spring-boot-starter-web, spring-boot-starter-validation</li>
 *   <li><b>移除的第三方依赖：</b>hutool相关import</li>
 * </ul>
 * 
 * @author 重构优化
 * @version 2.0 (移除hutool依赖，增强类型安全)
 */
@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截参数验证异常
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Void> validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = bindingResult.getFieldErrors().stream().findFirst().orElse(null);
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse("");
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result<Void> abstractException(HttpServletRequest request, AbstractException ex) {
        if (ex.getCause() != null) {
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return Results.failure(ex);
        }
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return Results.failure(ex);
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Throwable.class)
    public Result<Void> defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        return Results.failure();
    }

    private String getUrl(HttpServletRequest request) {
        if (!StringUtils.hasText(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}