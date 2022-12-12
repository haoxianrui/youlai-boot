package com.youlai.system.enums;

import com.youlai.system.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author haoxr
 * @date 2022/10/14
 */
public enum GenderEnum implements IBaseEnum<Integer> {

    MALE(1, "男"),
    FEMALE (2, "女");

    @Getter
    private Integer value;

    @Getter
    private String label;

    GenderEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
