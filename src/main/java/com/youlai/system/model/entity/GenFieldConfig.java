package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.youlai.system.common.base.BaseEntity;
import com.youlai.system.enums.FormTypeEnum;
import com.youlai.system.enums.QueryTypeEnum;
import lombok.Data;

/**
 * 字段配置实体
 *
 * @author Ray
 * @since 2.10.0
 */
@TableName(value = "gen_field_config")
@Data
public class GenFieldConfig extends BaseEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的配置ID
     */
    private Long configId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段描述
     */
    private String fieldComment;

    /**
     * 表单类型
     */
    private FormTypeEnum formType;

    /**
     * 查询方式
     */
    private QueryTypeEnum queryType;

    /**
     * 是否在列表显示
     */
    private Boolean isShowInList;

    /**
     * 是否在表单显示
     */
    private Boolean isShowInForm;

    /**
     * 是否在查询条件显示
     */
    private Boolean isShowInQuery;

    /**
     * 是否必填
     */
    private Boolean isRequired;


}