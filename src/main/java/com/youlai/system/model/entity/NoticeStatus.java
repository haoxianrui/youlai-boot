package com.youlai.system.model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;

/**
 * 用户公告状态实体对象
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Getter
@Setter
@TableName("sys_notice_status")
public class NoticeStatus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 公共通知id
     */
    private Long noticeId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 读取状态，0未读，1已读取
     */
    private Long readStatus;
    /**
     * 用户阅读时间
     */
    private LocalDateTime readTiem;
}
