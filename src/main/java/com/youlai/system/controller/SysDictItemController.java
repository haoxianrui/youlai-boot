package com.youlai.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import com.youlai.system.pojo.form.DictItemForm;
import com.youlai.system.pojo.query.DictItemPageQuery;
import com.youlai.system.pojo.vo.DictItemPageVO;
import com.youlai.system.service.SysDictItemService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "字典数据接口")
@RestController
@RequestMapping("/api/v1/dict/items")
@RequiredArgsConstructor
public class SysDictItemController {

    private final SysDictItemService dictItemService;

    @Operation(summary = "字典数据分页列表")
    @GetMapping("/pages")
    public PageResult<DictItemPageVO> listDictItemPages(
            @ParameterObject DictItemPageQuery queryParams
    ) {
        Page<DictItemPageVO> result = dictItemService.listDictItemPages(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典数据表单数据")
    @GetMapping("/{id}/form")
    public Result<DictItemForm> getDictItemForm(
            @Parameter(name ="字典ID") @PathVariable Long id
    ) {
        DictItemForm formData = dictItemService.getDictItemForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增字典数据")
    @PostMapping
    public Result saveDictItem(
            @RequestBody DictItemForm DictItemForm
    ) {
        boolean result = dictItemService.saveDictItem(DictItemForm);
        return Result.judge(result);
    }

    @Operation(summary = "修改字典数据")
    @PutMapping("/{id}")
    public Result updateDictItem(
            @PathVariable Long id,
            @RequestBody DictItemForm DictItemForm
    ) {
        boolean status = dictItemService.updateDictItem(id, DictItemForm);
        return Result.judge(status);
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{ids}")
    public Result deleteDictItems(
            @Parameter(name ="字典ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        boolean result = dictItemService.deleteDictItems(ids);
        return Result.judge(result);
    }

}
