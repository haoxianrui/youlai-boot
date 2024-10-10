package com.youlai.boot.shared.sms.service;

/**
 * 短信服务接口层
 * <p>
 * SMS = Short Message Service 短信服务
 *
 * @author Ray
 * @since 2024/8/17
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param mobile        手机号 13388886666
     * @param templateCode  短信模板 SMS_194640010
     * @param templateParam 模板参数 "[{"code":"123456"}]"
     * @return boolean 是否发送成功
     */
    boolean sendSms(String mobile, String templateCode, String templateParam);
}
