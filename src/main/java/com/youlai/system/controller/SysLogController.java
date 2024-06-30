package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.model.query.LogPageQuery;
import com.youlai.system.model.query.RolePageQuery;
import com.youlai.system.model.vo.LogPageVO;
import com.youlai.system.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 日志控制层
 *
 * @author Ray
 * @since 2.10.0
 */
@Tag(name = "08.日志接口")
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService logService;

    @Operation(summary = "日志分页列表")
    @GetMapping("/page")
    public PageResult<LogPageVO> listPagedLogs(
             LogPageQuery queryParams
    ) {
        Page<LogPageVO> result = logService.listPagedLogs(queryParams);
        return PageResult.success(result);
    }

}
