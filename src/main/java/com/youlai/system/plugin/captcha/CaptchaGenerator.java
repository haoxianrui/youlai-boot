package com.youlai.system.plugin.captcha;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import com.youlai.system.model.dto.CaptchaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码自动装配配置
 *
 * @author haoxr
 * @since 2023/11/24
 */
@Configuration
public class CaptchaGenerator {

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 验证码文字生成器
     *
     * @return CodeGenerator
     */
    @Bean
    public CodeGenerator codeGenerator() {
        String codeType = captchaProperties.getCode().getType();
        int codeLength = captchaProperties.getCode().getLength();
        if ("math".equalsIgnoreCase(codeType)) {
            return new MathGenerator(codeLength);
        } else if ("random".equalsIgnoreCase(codeType)) {
            return new RandomGenerator(codeLength);
        } else {
            throw new IllegalArgumentException("Invalid captcha generator type: " + codeType);
        }
    }


    /**
     * 生成验证码
     *
     * @return CaptchaModel 验证码
     */
    public CaptchaModel generate() {
        AbstractCaptcha captcha = getCaptcha();
        captcha.createCode();
        return new CaptchaModel(captcha.getCode(), captcha.getImageBase64Data());
    }

    /**
     * 验证码类
     *
     * @return AbstractCaptcha
     */
    public AbstractCaptcha getCaptcha() {
        AbstractCaptcha captcha = null;

        String type = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();


        if ("circle".equalsIgnoreCase(type)) {
            captcha = new CircleCaptcha(width, height, codeLength, interfereCount);
        } else if ("gif".equalsIgnoreCase(type)) {
            return null;
        } else if ("line".equalsIgnoreCase(type)) {
            return null;
        } else if ("shear".equalsIgnoreCase(type)) {
            return null;
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + type);
        }

        captcha.setGenerator(codeGenerator());
        return captcha;
    }


}
