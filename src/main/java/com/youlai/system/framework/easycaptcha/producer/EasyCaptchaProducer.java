package com.youlai.system.framework.easycaptcha.producer;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import com.youlai.system.framework.easycaptcha.config.EasyCaptchaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;

/**
 * 验证码生成器
 *
 * @author: haoxr
 * @date: 2023/03/24
 */

@Component
@RequiredArgsConstructor
public class EasyCaptchaProducer {
    private final EasyCaptchaConfig easyCaptchaConfig;

    public Captcha getCaptcha() {
        Captcha captcha;
        int width = easyCaptchaConfig.getWidth();
        int height = easyCaptchaConfig.getHeight();
        int length = easyCaptchaConfig.getLength();
        String fontName = easyCaptchaConfig.getFontName();

        switch (easyCaptchaConfig.getType()) {
            case ARITHMETIC:
                captcha = new ArithmeticCaptcha(width, height);
                //固定设置为两位，图片为算数运算表达式
                captcha.setLen(2);
                break;
            case CHINESE:
                captcha = new ChineseCaptcha(width, height);
                captcha.setLen(length);
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(width, height);
                captcha.setLen(length);
                break;
            case GIF:
                captcha = new GifCaptcha(width, height);//最后一位是位数
                captcha.setLen(length);
                break;
            case SPEC:
                captcha = new SpecCaptcha(width, height);
                captcha.setLen(length);
                break;
            default:
                throw new RuntimeException("验证码配置信息错误！正确配置查看 CaptchaTypeEnum ");
        }
        captcha.setFont(new Font(fontName, easyCaptchaConfig.getFontStyle(), easyCaptchaConfig.getFontSize()));
        return captcha;

    }


}
