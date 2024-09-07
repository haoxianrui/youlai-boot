package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.NoticeStatus;
import com.youlai.boot.system.model.vo.NoticeStatusVO;
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
public interface NoticeStatusMapper extends BaseMapper<NoticeStatus> {

    /**
     * 获取未读的通知公告
     * @param userId 用户ID
     * @return 公告列表
     */
    List<NoticeStatusVO> listUnreadNotices(@Param("userId")Long userId);
}
