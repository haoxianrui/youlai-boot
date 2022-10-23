package com.youlai.system.util;

import cn.hutool.json.JSONUtil;
import com.youlai.system.common.result.Result;
import com.youlai.system.common.result.ResultCode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * response 响应工具类
 *
 * @author haoxr
 * @date 2022/10/18
 */
public class ResponseUtils {

    /**
     * 异常消息返回(适用过滤器异常响应)
     *
     * @param response
     * @param resultCode
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode) {
        switch (resultCode) {
            case ACCESS_UNAUTHORIZED:
            case TOKEN_INVALID_OR_EXPIRED:
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                break;
            case TOKEN_ACCESS_FORBIDDEN:
                response.setStatus(HttpStatus.FORBIDDEN.value());
                break;
            default:
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                break;
        }
        response.setCharacterEncoding("UTF-8");
        try {
            String bodyJsonStr = JSONUtil.toJsonStr(Result.failed(resultCode));
            PrintWriter printWriter = response.getWriter();
            printWriter.print(bodyJsonStr);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
        }
    }


}
