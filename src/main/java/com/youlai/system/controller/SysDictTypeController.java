package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.pojo.Option;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.pojo.form.DictTypeForm;
import com.youlai.system.pojo.query.DictTypePageQuery;
import com.youlai.system.pojo.vo.dict.DictTypePageVO;
import com.youlai.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation; 
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典类型接口")
@RestController
@RequestMapping("/api/v1/dict/types")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    @Operation(summary = "字典类型分页列表")
    @GetMapping("/pages")
    public PageResult<DictTypePageVO> listDictTypePages(
            @ParameterObject DictTypePageQuery queryParams
    ) {
        Page<DictTypePageVO> result = dictTypeService.listDictTypePages(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典类型表单详情")
    @GetMapping("/{id}/form")
    public Result<DictTypeForm> getDictTypeFormData(
            @Parameter(name ="字典ID") @PathVariable Long id
    ) {
        DictTypeForm dictTypeForm = dictTypeService.getDictTypeFormData(id);
        return Result.success(dictTypeForm);
    }

    @Operation(summary = "新增字典类型")
    @PostMapping
    public Result saveDictType(@RequestBody DictTypeForm dictTypeForm) {
        boolean result = dictTypeService.saveDictType(dictTypeForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/{id}")
    public Result updateDict(@PathVariable Long id, @RequestBody DictTypeForm dictTypeForm) {
        boolean status = dictTypeService.updateDictType(id, dictTypeForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{ids}")
    public Result deleteDictTypes(
            @Parameter(name ="字典类型ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = dictTypeService.deleteDictTypes(ids);
        return Result.judge(result);
    }

    @Operation(summary = "获取字典类型的数据项")
    @GetMapping("/{typeCode}/items")
    public Result<List<Option>> listDictItemsByTypeCode(
            @Parameter(name ="字典类型编码") @PathVariable String typeCode
    ) {
        List<Option> list = dictTypeService.listDictItemsByTypeCode(typeCode);
        return Result.success(list);
    }
}
