package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.system.mapper.NoticeStatusMapper;
import com.youlai.boot.system.model.entity.NoticeStatus;
import com.youlai.boot.system.model.vo.NoticeStatusVO;
import com.youlai.boot.system.service.NoticeService;
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

    private final NoticeService noticeService;

    @Override
    public List<NoticeStatusVO> listNotices(Integer count) {
        LambdaQueryWrapper<NoticeStatus> queryWrapper =  new LambdaQueryWrapper<>();
        //获取当前用户
        queryWrapper.eq(NoticeStatus::getUserId, 1L);
        return null;
    }
}
