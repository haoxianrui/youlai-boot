package com.youlai.system.framework.easycaptcha.service;

import cn.hutool.core.util.IdUtil;
import com.wf.captcha.base.Captcha;
import com.youlai.system.common.constant.SecurityConstants;
import com.youlai.system.framework.easycaptcha.config.EasyCaptchaConfig;
import com.youlai.system.framework.easycaptcha.producer.EasyCaptchaProducer;
import com.youlai.system.pojo.dto.CaptchaResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * EasyCaptcha 业务类
 *
 * @author: haoxr
 * @date: 2023/03/24
 */
@Service
@RequiredArgsConstructor
public class EasyCaptchaService {

    private final EasyCaptchaProducer easyCaptchaProducer;

    private final RedisTemplate redisTemplate;

    private final EasyCaptchaConfig easyCaptchaConfig;

    /**
     * 获取验证码
     *
     * @return
     */
    public CaptchaResult getCaptcha() {
        // 获取验证码
        Captcha captcha = easyCaptchaProducer.getCaptcha();
        String captchaText = captcha.text(); // 验证码文本
        String captchaBase64 = captcha.toBase64(); // 验证码图片Base64字符串

        // 验证码文本缓存至Redis，用于登录校验
        String verifyCodeKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(SecurityConstants.VERIFY_CODE_CACHE_PREFIX + verifyCodeKey, captchaText,
                easyCaptchaConfig.getTtl(), TimeUnit.SECONDS);

        CaptchaResult captchaResult = CaptchaResult.builder()
                .verifyCodeKey(verifyCodeKey)
               .verifyCodeBase64(captchaBase64)
                .build();
        return captchaResult;
    }

}
