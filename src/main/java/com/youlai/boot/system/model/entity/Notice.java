package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
/**
 * 通知公告实体对象
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Getter
@Setter
@TableName("sys_notice")
public class Notice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知类型
     */
    private Integer noticeType;
    /**
     * 发布人
     */
    private Long releaseBy;
    /**
     * 优先级(0-低 1-中 2-高)
     */
    private Integer priority;
    /**
     * 目标类型(0-全体 1-指定)
     */
    private Integer tarType;
    /**
     * 目标ID
     */
    private String tarIds;
    /**
     * 发布状态(0-未发布 1已发布 2已撤回)
     */
    private Integer releaseStatus;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseTime;
    /**
     * 撤回时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recallTime;
    /**
     * 创建人ID
     */
    private Long createBy;
    /**
     * 更新人ID
     */
    private Long updateBy;
    /**
     * 逻辑删除标识(0-未删除 1-已删除)
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
