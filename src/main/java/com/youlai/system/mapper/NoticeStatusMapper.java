package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.entity.NoticeStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.query.NoticeStatusQuery;
import com.youlai.system.model.vo.NoticeStatusVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户公告状态Mapper接口
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Mapper
public interface NoticeStatusMapper extends BaseMapper<NoticeStatus> {

    /**
     * 获取用户公告状态分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return
     */
    Page<NoticeStatusVO> getNoticeStatusPage(Page<NoticeStatusVO> page, NoticeStatusQuery queryParams);

}
