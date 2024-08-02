package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.enums.LogModuleEnum;
import com.youlai.system.model.form.GenConfigForm;
import com.youlai.system.model.query.TablePageQuery;
import com.youlai.system.model.vo.GeneratorPreviewVO;
import com.youlai.system.model.vo.TablePageVO;
import com.youlai.system.plugin.syslog.annotation.LogAnnotation;
import com.youlai.system.service.GeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 代码生成器控制层
 *
 * @author Ray
 * @since 2.10.0
 */
@Tag(name = "09.代码生成")
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;

    @Operation(summary = "获取数据表分页列表")
    @GetMapping("/table/page")
    @LogAnnotation(value = "代码生成分页列表", module = LogModuleEnum.OTHER)
    public PageResult<TablePageVO> getTablePage(
            TablePageQuery queryParams
    ) {
        Page<TablePageVO> result = generatorService.getTablePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "获取代码生成配置")
    @GetMapping("/{tableName}/config")
    public Result<GenConfigForm> getGenConfigFormData(
            @Parameter(description = "表名", example = "sys_user") @PathVariable String tableName
    ) {
        GenConfigForm formData = generatorService.getGenConfigFormData(tableName);
        return Result.success(formData);
    }

    @Operation(summary = "保存代码生成配置")
    @PostMapping("/{tableName}/config")
    @LogAnnotation(value = "生成代码", module = LogModuleEnum.OTHER)
    public Result saveGenConfig(@RequestBody GenConfigForm formData) {
        generatorService.saveGenConfig(formData);
        return Result.success();
    }

    @Operation(summary = "删除代码生成配置")
    @DeleteMapping("/{tableName}/config")
    public Result deleteGenConfig(
            @Parameter(description = "表名", example = "sys_user") @PathVariable String tableName
    ) {
        generatorService.deleteGenConfig(tableName);
        return Result.success();
    }

    @Operation(summary = "获取预览生成代码")
    @GetMapping("/{tableName}/preview")
    @LogAnnotation(value = "预览生成代码", module = LogModuleEnum.OTHER)
    public Result<List<GeneratorPreviewVO>> getTablePreviewData(@PathVariable String tableName) {
        List<GeneratorPreviewVO> list = generatorService.getTablePreviewData(tableName);
        return Result.success(list);
    }

    @Operation(summary = "下载代码")
    @GetMapping("/{tableName}/download")
    @LogAnnotation(value = "下载代码", module = LogModuleEnum.OTHER)
    public void downloadZip(HttpServletResponse response, @PathVariable String tableName) throws IOException {
        String[] tableNames = tableName.split(",");
        byte[] data = generatorService.downloadCode(tableNames);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"youlai-admin-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setDateHeader("Expires", 0);
        IOUtils.write(data, response.getOutputStream());
    }
}
