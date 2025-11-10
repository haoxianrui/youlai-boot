package com.youlai.boot.platform.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI 命令审计记录
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@TableName("ai_command_audit")
public class AiCommandAudit {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 原始命令
     */
    private String originalCommand;

    /**
     * 解析后的函数名称
     */
    private String functionName;

    /**
     * 函数参数（JSON）
     */
    private String functionArguments;

    /**
     * 执行状态：pending, success, failed
     */
    private String executeStatus;

    /**
     * 执行结果（JSON）
     */
    private String executeResult;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 影响的记录数
     */
    private Integer affectedRows;

    /**
     * 是否危险操作
     */
    private Boolean isDangerous;

    /**
     * 是否需要确认
     */
    private Boolean requiresConfirmation;

    /**
     * 用户是否确认
     */
    private Boolean userConfirmed;

    /**
     * 幂等性令牌
     */
    private String idempotencyKey;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 当前路由
     */
    private String currentRoute;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 备注
     */
    private String remark;
}



