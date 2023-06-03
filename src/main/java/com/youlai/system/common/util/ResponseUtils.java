package com.youlai.system.common.util;

import cn.hutool.json.JSONUtil;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.result.ResultCode;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应工具类
 *
 * @author haoxr
 * @since 2022/10/18
 */
public class ResponseUtils {

    /**
     * 异常消息返回(适用过滤器中处理异常响应)
     *
     * @param response
     * @param resultCode
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode) throws IOException {
        switch (resultCode) {
            case ACCESS_UNAUTHORIZED:
            case TOKEN_INVALID:
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                break;
            case TOKEN_ACCESS_FORBIDDEN:
                response.setStatus(HttpStatus.FORBIDDEN.value());
                break;
            default:
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                break;
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSONUtil.toJsonStr(Result.failed(resultCode)));
    }


}
