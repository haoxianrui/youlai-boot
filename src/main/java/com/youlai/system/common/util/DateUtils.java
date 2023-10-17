
package com.youlai.system.common.util;

import cn.hutool.core.collection.CollectionUtil;

import java.util.List;

/**
 * 日期工具类
 *
 * @author haoxr
 * @since 2.4.2
 */
public class DateUtils {


    /**
     * 格式化日期范围
     *
     * @param createTimeRange
     * @return
     */
    public static List<String> formatDateRange(List<String> createTimeRange) {
        if (CollectionUtil.isNotEmpty(createTimeRange) && createTimeRange.size() == 2) {
            createTimeRange.set(0, createTimeRange.get(0) + " 00:00:00");
            createTimeRange.set(1, createTimeRange.get(1) + " 23:59:59");
            return createTimeRange;
        } else {
            return null;
        }
    }
}
