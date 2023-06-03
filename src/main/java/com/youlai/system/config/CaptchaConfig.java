package com.youlai.system.config;

import com.youlai.system.common.enums.CaptchaTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

/**
 * EasyCaptcha 配置类
 * 
 * @author haoxr
 * @since 2023/03/24
 */
@ConfigurationProperties(prefix = "easy-captcha")
@Configuration
@Data
public class CaptchaConfig {

    /**
     * 验证码类型
     */
    private CaptchaTypeEnum type = CaptchaTypeEnum.ARITHMETIC;


    /**
     * 验证码缓存过期时间(单位:秒)
     */
    private long ttl = 120l;

    /**
     * 验证码内容长度
     */
    private int length = 4;
    /**
     * 验证码宽度
     */
    private int width = 120;
    /**
     * 验证码高度
     */
    private int height = 36;


    /**
     * 验证码字体
     */
    private String fontName = "Verdana";

    /**
     * 字体风格
     */
    private Integer fontStyle = Font.PLAIN;

    /**
     * 字体大小
     */
    private int fontSize = 20;


}
