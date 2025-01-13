package com.youlai.boot.shared.sms.service;

import com.youlai.boot.shared.sms.enums.SmsTypeEnum;

/**
 * 短信服务接口层
 *
 * @author Ray.Hao
 * @since 2024/8/17
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param mobile        手机号 13388886666
     * @param smsType  短信模板 SMS_194640010
     * @param templateParam 模板参数 "[{"code":"123456"}]"
     * @return boolean 是否发送成功
     */
    boolean sendSms(String mobile, SmsTypeEnum smsType, String templateParam);
}
