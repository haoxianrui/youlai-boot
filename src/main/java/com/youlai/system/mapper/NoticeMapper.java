package com.youlai.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.entity.Notice;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.query.NoticeQuery;
import com.youlai.system.model.vo.NoticeVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper接口
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 获取通知公告分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return
     */
    Page<NoticeVO> getNoticePage(Page<NoticeVO> page, NoticeQuery queryParams);

}
