package com.youlai.boot.common.util;

import com.youlai.boot.common.constant.SymbolConstant;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用工具类
 *
 * @author Theo
 * @since 2024-9-1 23:42:33
 * @version 1.0.0
 */
public class CommonUtil {

    private CommonUtil(){}



    /**
     * 将List转换为字符串
     *
     * @param list      List
     * @param separator 分隔符
     * @return 字符串
     */
    public static String listToStr(List<String> list, String separator) {
        return list.stream().collect(Collectors.joining(separator));
    }

    /**
     * 将字符串转换为List
     *
     * @param list      List
     * @return List
     */
    public static String listToStr(List<String> list) {
        return listToStr(list, SymbolConstant.COMMA);
    }

    /**
     * 将字符串转换为List
     *
     * @param str       字符串
     * @return List
     */
    public static List<String> strToList(String str) {
        return strToList(str, SymbolConstant.COMMA);
    }

    /**
     * 将字符串转换为List
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return List
     */
    public static List<String> strToList(String str, String separator) {
        return List.of(str.split(separator));
    }


    public static String delHtmlTags(String htmlStr) {
        return htmlStr.replaceAll("<[^>]+>", "");
    }
}
