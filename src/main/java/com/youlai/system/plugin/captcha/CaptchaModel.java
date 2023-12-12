package com.youlai.system.plugin.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaModel {
    /**
     * 验证码编码
     */
    private String code;

    /**
     * 验证码图片Base64
     */
    private String base64;

}
