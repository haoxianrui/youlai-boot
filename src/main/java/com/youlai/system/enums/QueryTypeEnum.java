package com.youlai.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.youlai.system.common.base.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 查询类型枚举
 *
 * @author Ray
 * @since 2.10.0
 */
@Getter
@RequiredArgsConstructor
public enum QueryTypeEnum implements IBaseEnum<Integer> {


    EQ(1, "="),


    NE(2, "!="),


    GT(3, ">"),


    GE(4, ">="),

    LT(5, "<"),

    LE(6, "<="),

    BETWEEN(7, "BETWEEN"),

    LIKE(8, "LIKE '%s%'"),

    LIKE_LEFT(9, "LIKE '%s'"),

    LIKE_RIGHT(10, "LIKE 's%'"),

    IN(11, "IN"),

    NOT_IN(12, "NOT IN"),

    IS_NULL(13, "IS NULL"),

    IS_NOT_NULL(14, "IS NOT NULL")
    ;

    //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    @EnumValue
    private final Integer value;

    // @JsonValue //  表示对枚举序列化时返回此字段
    private final String label;

}
