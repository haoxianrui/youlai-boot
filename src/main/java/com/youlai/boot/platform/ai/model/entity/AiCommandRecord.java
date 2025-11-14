package com.youlai.boot.platform.ai.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI 命令记录实体（合并解析和执行记录）
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_command_record")
public class AiCommandRecord extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 原始命令 */
    private String originalCommand;

    // ==================== 解析相关字段 ====================

    /** AI 供应商（qwen/openai/deepseek等） */
    private String provider;

    /** AI 模型（qwen-plus/qwen-max/gpt-4-turbo等） */
    private String model;

    /** 解析是否成功 */
    private Boolean parseSuccess;

    /** 解析出的函数调用列表（JSON） */
    private String functionCalls;

    /** AI 的理解说明 */
    private String explanation;

    /** 置信度（0.00-1.00） */
    private BigDecimal confidence;

    /** 解析错误信息 */
    private String parseErrorMessage;

    /** 输入 Token 数量 */
    private Integer inputTokens;

    /** 输出 Token 数量 */
    private Integer outputTokens;

    /** 总 Token 数量 */
    private Integer totalTokens;

    /** 解析耗时（毫秒） */
    private Long parseTime;

    // ==================== 执行相关字段 ====================

    /** 执行的函数名称 */
    private String functionName;

    /** 函数参数（JSON） */
    private String functionArguments;

    /** 执行状态：pending, success, failed */
    private String executeStatus;

    /** 执行结果（JSON） */
    private String executeResult;

    /** 执行错误信息 */
    private String executeErrorMessage;

    /** 影响的记录数 */
    private Integer affectedRows;

    /** 是否危险操作 */
    private Boolean isDangerous;

    /** 是否需要确认 */
    private Boolean requiresConfirmation;

    /** 用户是否确认 */
    private Boolean userConfirmed;

    /** 幂等性令牌（防止重复执行） */
    private String idempotencyKey;

    /** 执行耗时（毫秒） */
    private Long executionTime;

    // ==================== 通用字段 ====================

    /** IP 地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 当前页面路由 */
    private String currentRoute;

    /** 备注 */
    private String remark;
}


