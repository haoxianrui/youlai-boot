package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典实体
 *
 * @author haoxr
 * @since 2022/12/17
 */
@Data
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = true)
public class Dict extends BaseEntity {

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String name;


    /**
     * 状态（1：启用, 0：停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}