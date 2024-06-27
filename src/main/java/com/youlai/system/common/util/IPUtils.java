package com.youlai.system.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP工具类
 * <p>
 * 获取客户端IP地址和IP地址对应的地理位置信息
 * <p>
 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
 * </p>
 *
 * @author Ray
 * @since 2.10.0
 */
@Slf4j
public class IPUtils {

    private static final String DB_PATH = "src/main/resources/data/ip2region.xdb";

    /**
     * 获取IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            if (request == null) {
                return "";
            }
            ip = request.getHeader("x-forwarded-for");
            if (checkIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (checkIp(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR, {}", e.getMessage());
        }

        // 使用代理，则获取第一个IP地址
        if (StrUtil.isNotBlank(ip) && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    private static boolean checkIp(String ip) {
        String unknown = "unknown";
        return StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip);
    }

    /**
     * 获取本机的IP地址
     *
     * @return 本机IP地址
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error, {}", e.getMessage());
        }
        return null;
    }

    /**
     * 根据IP地址获取地理位置信息
     *
     * @param ip IP地址
     * @return 地理位置信息
     */
    public static String getRegion(String ip) {
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithFileOnly(DB_PATH);
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil ERROR, {}", e.getMessage());
            return null;
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    log.error("IpRegionUtil close ERROR, {}", e.getMessage());
                }
            }
        }
    }
}
