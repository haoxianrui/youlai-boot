package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.UserNotice;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.model.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户公告状态Mapper接口
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Mapper
public interface UserNoticeMapper extends BaseMapper<UserNotice> {

    /**
     * 获取未读的通知公告
     * @param userId 用户ID
     * @return 公告列表
     */
    List<UserNoticePageVO> listUnreadNotices(@Param("userId")Long userId);

    /**
     * 分页获取我的通知公告
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return 通知公告分页列表
     */
    IPage<UserNoticePageVO> getMyNoticePage(Page<NoticeVO> page, @Param("queryParams") NoticePageQuery queryParams);
}
