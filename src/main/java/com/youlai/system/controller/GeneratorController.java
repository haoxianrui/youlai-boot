package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.TableColumnVO;
import com.youlai.system.model.vo.TableGeneratePreviewVO;
import com.youlai.system.model.vo.TablePageVO;
import com.youlai.system.service.GeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "09.代码生成")
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;

    @Operation(summary = "获取数据表分页列表")
    @GetMapping("/table/page")
    public PageResult<TablePageVO> getTablePage(
            TablePageQuery queryParams
    ) {
        Page<TablePageVO> result = generatorService.getTablePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "获取数据表字段列表")
    @GetMapping("/table/{tableName}/columns")
    public Result<List<TableColumnVO>> getTableColumns(
            @Parameter(description = "表名", example = "sys_user") @PathVariable String tableName
    ) {
        List<TableColumnVO> list = generatorService.getTableColumns(tableName);
        return Result.success(list);
    }


    @Operation(summary = "获取预览生成代码")
    @GetMapping("/table/{tableName}/preview")
    public Result<List<TableGeneratePreviewVO>> getTablePreviewData(@PathVariable String tableName) {
        List<TableGeneratePreviewVO> list = generatorService.getTablePreviewData(tableName);
        return Result.success(list);
    }

}
