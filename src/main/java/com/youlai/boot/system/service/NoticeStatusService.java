package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.NoticeStatus;
import com.youlai.boot.system.model.vo.NoticeStatusVO;

import java.util.List;

/**
 * 用户公告状态服务类
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
public interface NoticeStatusService extends IService<NoticeStatus> {

    /**
     * 获取未读的通知公告
     * @return 公告列表
     */
    List<NoticeStatusVO> listUnreadNotices();

    /**
     * 全部标记为已读
     * @return 是否成功
     */
    boolean readAll();
}
