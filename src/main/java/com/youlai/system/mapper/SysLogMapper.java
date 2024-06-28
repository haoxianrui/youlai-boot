package com.youlai.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.system.model.query.RolePageQuery;
import com.youlai.system.model.vo.LogPageVO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 系统日志 数据库访问层
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    Page<LogPageVO> listPagedLogs(Page page, RolePageQuery queryParams);
}




