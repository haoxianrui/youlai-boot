
package com.youlai.system.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 日期工具类
 *
 * @author haoxr
 * @since 2.4.2
 */
public class DateUtils {


    /**
     * 将日期格式化为数据库日期格式 (yyyy-MM-dd HH:mm:ss) 并更新对象中指定的起始时间字段和结束时间字段。
     *
     * @param obj               要处理的对象
     * @param startTimeFieldName 起始时间字段名
     * @param endTimeFieldName   结束时间字段名
     */
    public static void formatDateTimeForDatabase(Object obj, String startTimeFieldName, String endTimeFieldName) {
        Field startTimeField = ReflectUtil.getField(obj.getClass(), startTimeFieldName);
        Field endTimeField = ReflectUtil.getField(obj.getClass(), endTimeFieldName);

        if (startTimeField != null) {
            processDateTimeField(obj, startTimeField, startTimeFieldName, "yyyy-MM-dd 00:00:00");
        }

        if (endTimeField != null) {
            processDateTimeField(obj, endTimeField, endTimeFieldName, "yyyy-MM-dd 23:59:59");
        }
    }

    private static void processDateTimeField(Object obj, Field field, String fieldName, String targetPattern) {
        Object fieldValue = ReflectUtil.getFieldValue(obj, fieldName);
        if (fieldValue != null) {
            String pattern = field.isAnnotationPresent(DateTimeFormat.class) ?
                    field.getAnnotation(DateTimeFormat.class).pattern() : "yyyy-MM-dd";
            DateTime dateTime = DateUtil.parse(StrUtil.toString(fieldValue), pattern);
            ReflectUtil.setFieldValue(obj, fieldName, dateTime.toString(targetPattern));
        }
    }
}
