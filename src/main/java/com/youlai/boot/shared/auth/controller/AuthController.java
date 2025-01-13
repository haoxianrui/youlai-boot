package com.youlai.boot.shared.auth.controller;

import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.shared.auth.service.AuthService;
import com.youlai.boot.shared.auth.model.CaptchaInfo;
import com.youlai.boot.core.security.model.AuthenticationToken;
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

    @Operation(summary = "获取登录验证码")
    @GetMapping("/captcha")
    public Result<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = authService.getCaptcha();
        return Result.success(captcha);
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    @Log(value = "登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> login(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password
    ) {
        AuthenticationToken authenticationToken = authService.login(username, password);
        return Result.success(authenticationToken);
    }

    @Operation(summary = "注销登录")
    @DeleteMapping("/logout")
    @Log(value = "注销", module = LogModuleEnum.LOGIN)
    public Result<?> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "刷新访问令牌")
    @PostMapping("/refresh-token")
    public Result<?> refreshToken(
            @Parameter(description = "刷新令牌", example = "xxx.xxx.xxx") @RequestParam String refreshToken
    ) {
        AuthenticationToken authenticationToken = authService.refreshToken(refreshToken);
        return Result.success(authenticationToken);
    }

    @Operation(summary = "微信授权登录")
    @PostMapping("/login/wechat")
    @Log(value = "微信登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginByWechat(
            @Parameter(description = "微信授权码", example = "code") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginByWechat(code);
        return Result.success(loginResult);
    }

    @Operation(summary = "发送登录短信验证码")
    @PostMapping("/login/sms/code")
    public Result<Void> sendLoginVerifyCode(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile
    ) {
        authService.sendSmsLoginCode(mobile);
        return Result.success();
    }

    @Operation(summary = "短信验证码登录")
    @PostMapping("/login/sms")
    @Log(value = "短信验证码登录", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginBySms(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile,
            @Parameter(description = "验证码", example = "1234") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginBySms(mobile, code);
        return Result.success(loginResult);
    }
}
