package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.core.security.util.SecurityUtils;
import com.youlai.boot.system.mapper.NoticeStatusMapper;
import com.youlai.boot.system.model.entity.NoticeStatus;
import com.youlai.boot.system.model.query.NoticeQuery;
import com.youlai.boot.system.model.vo.NoticeStatusVO;
import com.youlai.boot.system.model.vo.NoticeVO;
import com.youlai.boot.system.service.NoticeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户公告状态服务实现类
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Service
@RequiredArgsConstructor
public class NoticeStatusServiceImpl extends ServiceImpl<NoticeStatusMapper, NoticeStatus> implements NoticeStatusService {

    private final NoticeStatusMapper noticeStatusMapper;

    /**
     * 获取未读的通知公告
     * @return 公告列表
     */
    @Override
    public List<NoticeStatusVO> listUnreadNotices() {
        //获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        return noticeStatusMapper.listUnreadNotices(userId);
    }

    /**
     * 全部标记为已读
     * @return 是否成功
     */
    @Override
    public boolean readAll() {
        Long userId = SecurityUtils.getUserId();
        LambdaUpdateWrapper<NoticeStatus> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoticeStatus::getUserId, userId);
        updateWrapper.set(NoticeStatus::getReadStatus, 1);
        return this.update(updateWrapper);
    }

    /**
     * 分页获取我的通知公告
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return 通知公告分页列表
     */
    @Override
    public IPage<NoticeStatusVO> getMyNoticePage(Page<NoticeVO> page, NoticeQuery queryParams) {
        return this.getBaseMapper().getMyNoticePage(new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),queryParams);
    }


}
