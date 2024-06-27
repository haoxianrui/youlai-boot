package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.youlai.system.enums.LogModuleEnum;
import lombok.Data;

/**
 * 系统日志 实体类
 *
 * @author Ray
 * @since 2.10.0
 */
@Data
public class SysLog implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志模块
     */
    private LogModuleEnum module;


    /**
     * 日志内容
     */
    private String content;

    /**
     * 请求路径
     */
    private String requestUri;

    /**
     * 请求方法
     */
    private String method;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 地区
     */
    private String region;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 终端系统
     */
    private String os;

    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;


    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}