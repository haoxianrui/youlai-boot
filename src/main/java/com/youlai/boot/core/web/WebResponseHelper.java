package com.youlai.boot.core.web;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * Web响应辅助类
 * <p>
 * 用于在过滤器、处理器等无法使用 @RestControllerAdvice 的场景中统一处理响应
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
@Slf4j
public class WebResponseHelper {

    /**
     * 写入错误响应
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        writeError(response, resultCode, null);
    }

    /**
     * 写入错误响应（带自定义消息）
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     * @param message    自定义消息
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode, String message) {
        try {
            // 设置HTTP状态码
            int httpStatus = mapHttpStatus(resultCode);
            response.setStatus(httpStatus);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            // 构建响应对象
            Result<?> result = message == null
                    ? Result.failed(resultCode)
                    : Result.failed(resultCode, message);

            // 写入响应
            JakartaServletUtil.write(response,
                    JSONUtil.toJsonStr(result),
                    MediaType.APPLICATION_JSON_VALUE
            );

        } catch (Exception e) {
            log.error("写入错误响应失败: resultCode={}, message={}", resultCode, message, e);
        }
    }

    /**
     * 根据业务结果码映射HTTP状态码
     *
     * @param resultCode 业务结果码
     * @return HTTP状态码
     */
    private static int mapHttpStatus(ResultCode resultCode) {
        return switch (resultCode) {
            case ACCESS_UNAUTHORIZED,
                    ACCESS_TOKEN_INVALID,
                    REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
    }
}

