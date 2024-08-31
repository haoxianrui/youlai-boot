package com.youlai.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.system.common.base.BaseEntity;

/**
 * 用户公告状态实体对象
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Getter
@Setter
@TableName("sys_notice_status")
public class NoticeStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公共通知id
     */
    private Long noticeId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 读取状态，0未读，1已读取
     */
    private Long readStatus;
    /**
     * 用户阅读时间
     */
    private LocalDateTime readTiem;
}
