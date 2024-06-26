package com.youlai.system.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *  IP工具类
 *
 * @author Ray
 * @since 2.10.0
 */
@Slf4j
public class IPUtils {


    /**
     * 获取客户端IP地址
     * 如果通过了多级反向代理，则取X-Forwarded-For头中第一个非unknown的有效IP地址
     *
     * @param request HttpServletRequest请求对象
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (isIpInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isIpInvalid(ip)) {
            ip = request.getRemoteAddr();
            // 检查是否为本地IP地址
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                ip = getLocalAddr();
            }
        }

        // 如果存在多个代理IP地址，取第一个非unknown的有效IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 检查IP地址是否无效
     *
     * @param ip IP地址字符串
     * @return 如果IP地址为空或"unknown"，则返回true，否则返回false
     */
    private static boolean isIpInvalid(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取本机IP地址
     *
     * @return 本机IP地址字符串
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

}
