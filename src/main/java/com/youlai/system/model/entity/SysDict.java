package com.youlai.system.model.entity;

import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 字典实体
 *
 * @author haoxr
 * @since 2022/12/17
 */
@Data
public class SysDict extends BaseEntity {

    /**
     * 类型名称
     */
    private String name;

    /**
     * 类型编码
     */
    private String code;

    /**
     * 状态(0:正常;1:禁用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}