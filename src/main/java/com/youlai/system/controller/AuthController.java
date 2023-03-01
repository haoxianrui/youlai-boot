package com.youlai.system.controller;


import com.youlai.system.common.result.Result;
import com.youlai.system.pojo.dto.LoginResult;
import com.youlai.system.framework.security.JwtTokenManager;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResult> login(
            @Parameter(name = "用户名",example = "admin") @RequestParam String username,
            @Parameter(name = "密码") @RequestParam String password
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username.toLowerCase().trim(),
                password
        );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成token
        String accessToken = "Bearer " + jwtTokenManager.createToken(authentication);
        LoginResult loginResult = LoginResult.builder()
                .accessToken(accessToken)
                .build();
        return Result.success(loginResult);
    }

    @Operation(summary = "注销")
    @DeleteMapping("/logout")
    public Result login() {
        SecurityContextHolder.clearContext();
        return Result.success("注销成功");
    }

}
