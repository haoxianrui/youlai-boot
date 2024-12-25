package com.youlai.boot.shared.auth.controller;

import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.shared.auth.model.RefreshTokenRequest;
import com.youlai.boot.shared.auth.service.AuthService;
import com.youlai.boot.shared.auth.model.CaptchaResponse;
import com.youlai.boot.shared.auth.model.AuthTokenResponse;
import com.youlai.boot.common.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制层
 *
 * @author Ray.Hao
 * @since 2022/10/16
 */
@Tag(name = "01.认证中心")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    @Log(value = "登录", module = LogModuleEnum.LOGIN)
    public Result<AuthTokenResponse> login(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password
    ) {
        AuthTokenResponse authTokenResponse = authService.login(username, password);
        return Result.success(authTokenResponse);
    }

    @Operation(summary = "注销")
    @DeleteMapping("/logout")
    @Log(value = "注销", module = LogModuleEnum.LOGIN)
    public Result<?> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = authService.getCaptcha();
        return Result.success(captcha);
    }

    @Operation(summary = "刷新token")
    @PostMapping("/refresh-token")
    public Result<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthTokenResponse authTokenResponse = authService.refreshToken(request);
        return Result.success(authTokenResponse);
    }

    @Operation(summary = "微信登录")
    @PostMapping("/wechat-login")
    @Log(value = "微信登录", module = LogModuleEnum.LOGIN)
    public Result<AuthTokenResponse> wechatLogin(
            @Parameter(description = "微信授权码", example = "code") @RequestParam String code
    ) {
        AuthTokenResponse loginResult = authService.wechatLogin(code);
        return Result.success(loginResult);
    }
}
