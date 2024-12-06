package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlai.boot.common.enums.LogModuleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志 实体类
 *
 * @author Ray
 * @since 2.10.0
 */
@TableName("sys_log")
@Data
public class Log implements Serializable {

    @Schema(description = "主键")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "日志模块")
    private LogModuleEnum module;

    @Schema(description = "请求方式")
    @TableField(value = "request_method")
    private String requestMethod;

    @Schema(description = "请求参数")
    @TableField(value = "request_params")
    private String requestParams;

    @Schema(description = "响应参数")
    @TableField(value = "response_content")
    private String responseContent;

    @Schema(description = "日志内容")
    private String content;

    @Schema(description = "请求路径")
    private String requestUri;

    @Schema(description = "IP 地址")
    private String ip;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "浏览器版本")
    private String browserVersion;

    @Schema(description = "终端系统")
    private String os;

    @Schema(description = "执行时间(毫秒)")
    private Long executionTime;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}