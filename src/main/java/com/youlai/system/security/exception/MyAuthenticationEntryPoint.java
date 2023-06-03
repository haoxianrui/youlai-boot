package com.youlai.system.security.exception;

import com.youlai.system.common.result.ResultCode;
import com.youlai.system.common.util.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证异常处理
 *
 * @author haoxr
 * @since 2022/10/18
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseUtils.writeErrMsg(response, ResultCode.TOKEN_INVALID);
    }
}
