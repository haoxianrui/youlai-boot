package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.youlai.system.common.base.BaseEntity;
import lombok.Data;

/**
 * 代码生成基础配置
 */
@TableName(value ="gen_config")
@Data
public class GenConfig extends BaseEntity {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 实体名
     */
    private String entityName;

    /**
     * 类描述
     */
    private String comment;

    /**
     * 上级菜单ID
     */
    private Long parentMenuId;

    /**
     * 作者
     */
    private String author;


    @TableLogic
    private Integer isDeleted;
}