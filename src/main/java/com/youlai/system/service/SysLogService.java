package com.youlai.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.model.entity.SysLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.system.model.query.RolePageQuery;
import com.youlai.system.model.vo.LogPageVO;

/**
 * 系统日志 服务接口
 *
 * @author Ray
 * @since 2.10.0
 */
public interface SysLogService extends IService<SysLog> {

    Page<LogPageVO> listPagedLogs(RolePageQuery queryParams);
}
