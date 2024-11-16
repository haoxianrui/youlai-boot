package com.youlai.boot.shared.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 验证码响应对象
 *
 * @author Ray Hao
 * @since 2023/03/24
 */
@Schema(description = "验证码响应对象")
@Data
@Builder
public class CaptchaResponse {

    @Schema(description = "验证码ID")
    private String captchaKey;

    @Schema(description = "验证码图片Base64字符串")
    private String captchaBase64;

}
