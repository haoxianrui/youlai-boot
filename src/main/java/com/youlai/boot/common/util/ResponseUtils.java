package com.youlai.boot.common.util;

import cn.hutool.json.JSONUtil;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.common.result.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
@Slf4j
public class ResponseUtils {


    /**
     * 异常消息返回(适用过滤器中处理异常响应)
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode) {
        int status = getHttpStatus(resultCode);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSONUtil.toJsonStr(Result.failed(resultCode));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }

    /**
     * 异常消息返回(适用过滤器中处理异常响应)
     *
     * @param response   HttpServletResponse
     * @param resultCode 响应结果码
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode, String message) {
        int status = getHttpStatus(resultCode);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSONUtil.toJsonStr(Result.failed(resultCode, message));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        } catch (IOException e) {
            log.error("响应异常处理失败", e);
        }
    }


    /**
     * 根据结果码获取HTTP状态码
     *
     * @param resultCode 结果码
     * @return HTTP状态码
     */
    private static int getHttpStatus(ResultCode resultCode) {
        return switch (resultCode) {
            case ACCESS_UNAUTHORIZED, ACCESS_TOKEN_INVALID, REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
    }

}
