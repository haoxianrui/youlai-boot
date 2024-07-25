package com.youlai.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    // 存储在数据库中的枚举属性值
    @EnumValue
    @JsonValue
    private final Integer value;

    // 序列化成 JSON 时的属性值
    private final String label;


    @JsonCreator
    public static QueryTypeEnum fromValue(Integer value) {
        for (QueryTypeEnum type : QueryTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

}
