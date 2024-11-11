package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.VisitCount;
import com.youlai.boot.system.model.entity.Log;
import com.youlai.boot.system.model.query.LogPageQuery;
import com.youlai.boot.system.model.vo.LogPageVO;
import com.youlai.boot.system.model.vo.VisitStatsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 系统日志 数据库访问层
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

    /**
     * 获取日志分页列表
     *
     * @param page
     * @param queryParams
     * @return
     */
    Page<LogPageVO> getLogPage(Page<LogPageVO> page, LogPageQuery queryParams);

    /**
     * 统计浏览数(PV)
     *
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @return
     */
    List<VisitCount> getPvCounts(String startDate, String endDate);

    /**
     * 统计IP数
     *
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @return
     */
    List<VisitCount> getIpCounts(String startDate, String endDate);

    /**
     * 获取浏览量(PV)统计数据
     *
     * @return
     */
    VisitStatsVO getPvStats();

    /**
     * 获取IP统计数据
     *
     * @return
     */
    VisitStatsVO getIpStats();
}




