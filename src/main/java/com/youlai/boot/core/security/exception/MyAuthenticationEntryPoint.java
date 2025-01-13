package com.youlai.boot.core.security.exception;

import com.youlai.boot.common.result.ResultCode;
import com.youlai.boot.common.util.ResponseUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 未认证异常处理器
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        int status = response.getStatus();
        if (status == HttpServletResponse.SC_NOT_FOUND) {
            // 资源不存在
            ResponseUtils.writeErrMsg(response, ResultCode.USER_RESOURCE_NOT_FOUND);
        } else {
            if (authException instanceof BadCredentialsException) {
                // 用户名或密码错误
                ResponseUtils.writeErrMsg(response, ResultCode.USER_PASSWORD_ERROR, authException.getMessage());
            } else {
                // 登录异常
                ResponseUtils.writeErrMsg(response, ResultCode.USER_LOGIN_EXCEPTION, authException.getMessage());
            }
        }
    }
}
