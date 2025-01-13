package com.youlai.boot.shared.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 验证码信息
 *
 * @author Ray。Hao
 * @since 2023/03/24
 */
@Schema(description = "验证码信息")
@Data
@Builder
public class CaptchaInfo {

    @Schema(description = "验证码缓存 Key")
    private String captchaKey;

    @Schema(description = "验证码图片Base64字符串")
    private String captchaBase64;

}
