package com.youlai.system.security.exception;

import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security访问异常处理器
 *
 * @author haoxr
 * @since 2022/10/18
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
    }
}
