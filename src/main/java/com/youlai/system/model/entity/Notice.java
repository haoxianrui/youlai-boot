package com.youlai.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;

/**
 * 通知公告实体对象
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
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
    private Long release;
    /**
     * 优先级(0-低 1-中 2-高)
     */
    private Integer priority;
    /**
     * 目标类型(0-全体 1-指定)
     */
    private Integer tarType;
    /**
     * 发布状态(0-未发布 1已发布 2已撤回)
     */
    private Integer sendStatus;
    /**
     * 发布时间
     */
    private LocalDateTime sendTime;
    /**
     * 撤回时间
     */
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
    private Integer isDelete;
}
