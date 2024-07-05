package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.query.LogPageQuery;
import com.youlai.system.model.vo.LogPageVO;
import com.youlai.system.model.vo.VisitStatsVO;
import com.youlai.system.model.vo.VisitTrendVO;
import com.youlai.system.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


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

    @Operation(summary = "获取访问趋势")
    @GetMapping("/visit-trend")
    public Result<VisitTrendVO> getVisitTrend(
            @Parameter(description = "开始时间", example = "yyyy-MM-dd") @RequestParam String startDate,
            @Parameter(description = "结束时间", example = "yyyy-MM-dd") @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        VisitTrendVO data = logService.getVisitTrend(start, end);
        return Result.success(data);
    }

    @Operation(summary = "获取统计数据")
    @GetMapping("/visit-stats")
    public Result<List<VisitStatsVO>> getVisitStats() {
        List<VisitStatsVO> list = logService.getVisitStats();
        return Result.success(list);
    }

}
