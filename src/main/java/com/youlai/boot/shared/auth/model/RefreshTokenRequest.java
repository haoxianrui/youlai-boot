package com.youlai.boot.shared.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌请求参数
 *
 * @author haoxr
 * @since 2024/11/11
 */
@Schema(description = "刷新令牌请求参数")
@Data
public class RefreshTokenRequest {

    @Schema(description = "刷新令牌")
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

}
