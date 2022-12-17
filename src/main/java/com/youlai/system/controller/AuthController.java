package com.youlai.system.controller;


import com.youlai.system.common.result.Result;
import com.youlai.system.pojo.dto.TokenResult;
import com.youlai.system.security.JwtTokenManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public Result<TokenResult> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username.toLowerCase().trim(),
                password
        );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成token
        String accessToken = "Bearer " + jwtTokenManager.createToken(authentication);
        TokenResult tokenResult = TokenResult.builder()
                .accessToken(accessToken)
                .build();
        return Result.success(tokenResult);
    }

    @ApiOperation(value = "注销")
    @DeleteMapping("/logout")
    public Result login() {
        SecurityContextHolder.clearContext();
        return Result.success("注销成功");
    }

}
