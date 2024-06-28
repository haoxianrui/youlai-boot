package com.youlai.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.system.model.entity.SysLog;
import com.youlai.system.model.query.RolePageQuery;
import com.youlai.system.model.vo.LogPageVO;
import com.youlai.system.service.SysLogService;
import com.youlai.system.mapper.SysLogMapper;
import org.springframework.stereotype.Service;

/**
 * 系统日志 服务实现类
 *
 * @author Ray
 * @since 2.10.0
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog>
        implements SysLogService {

    /**
     * 获取日志分页列表
     *
     * @param queryParams 查询参数
     * @return
     */
    @Override
    public Page<LogPageVO> listPagedLogs(RolePageQuery queryParams) {
        return this.baseMapper.listPagedLogs(new Page(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams);
    }
}




