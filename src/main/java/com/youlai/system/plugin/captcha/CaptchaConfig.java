package com.youlai.system.plugin.captcha;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
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
public class CaptchaConfig {

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 验证码文字生成器
     *
     * @return CodeGenerator
     */
    @Bean
    public CodeGenerator captchaGenerator() {
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
     * 验证码类
     *
     * @return AbstractCaptcha
     */
    @Bean
    public AbstractCaptcha abstractCaptcha() {
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

        captcha.setGenerator(captchaGenerator());
        return captcha;
    }


}
