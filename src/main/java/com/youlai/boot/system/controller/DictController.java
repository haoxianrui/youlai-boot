package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.common.result.PageResult;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictItemPageVO;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.system.service.DictItemService;
import com.youlai.boot.system.service.DictService;
import com.youlai.boot.system.service.WebSocketMessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 字典控制层
 *
 * @author Ray.Hao
 * @since 2.9.0
 */
@Tag(name = "06.字典接口")
@RestController
@SuppressWarnings("SpellCheckingInspection")
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;
    private final DictItemService dictItemService;
    private final WebSocketMessageService webSocketMessageService;

    //---------------------------------------------------
    // 字典相关接口
    //---------------------------------------------------
    @Operation(summary = "字典分页列表")
    @GetMapping("/page")
    @Log( value = "字典分页列表",module = LogModuleEnum.DICT)
    public PageResult<DictPageVO> getDictPage(
            DictPageQuery queryParams
    ) {
        Page<DictPageVO> result = dictService.getDictPage(queryParams);
        return PageResult.success(result);
    }


    @Operation(summary = "字典列表")
    @GetMapping
    public Result<List<Option<String>>> getDictList() {
        List<Option<String>> list = dictService.getDictList();
        return Result.success(list);
    }

    @Operation(summary = "字典表单数据")
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description = "字典ID") @PathVariable Long id
    ) {
        DictForm formData = dictService.getDictForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "新增字典")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:dict:add')")
    @RepeatSubmit
    public Result<?> saveDict(@Valid @RequestBody DictForm formData) {
        boolean result = dictService.saveDict(formData);
        // 发送字典更新通知
        if (result && formData.getCode() != null) {
            webSocketMessageService.sendDictUpdatedEvent(formData.getCode());
        }
        return Result.judge(result);
    }

    @Operation(summary = "修改字典")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPerm('sys:dict:edit')")
    public Result<?> updateDict(
            @PathVariable Long id,
            @RequestBody DictForm dictForm
    ) {
        boolean status = dictService.updateDict(id, dictForm);
        // 发送字典更新通知
        if (status && dictForm.getCode() != null) {
            webSocketMessageService.sendDictUpdatedEvent(dictForm.getCode());
        }
        return Result.judge(status);
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:dict:delete')")
    public Result<?> deleteDictionaries(
            @Parameter(description = "字典ID，多个以英文逗号(,)拼接") @PathVariable String ids
    ) {
        // 获取字典编码列表，用于发送删除通知
        List<String> dictCodes = dictService.getDictCodesByIds(Arrays.stream(ids.split(",")).toList());
        
        dictService.deleteDictByIds(Arrays.stream(ids.split(",")).toList());
        
        // 发送字典删除通知
        for (String dictCode : dictCodes) {
            webSocketMessageService.sendDictDeletedEvent(dictCode);
        }
        
        return Result.success();
    }


    //---------------------------------------------------
    // 字典项相关接口
    //---------------------------------------------------
    @Operation(summary = "字典项分页列表")
    @GetMapping("/{dictCode}/items/page")
    public PageResult<DictItemPageVO> getDictItemPage(
            @PathVariable String dictCode,
            DictItemPageQuery queryParams
    ) {
        queryParams.setDictCode(dictCode);
        Page<DictItemPageVO> result = dictItemService.getDictItemPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "字典项列表")
    @GetMapping("/{dictCode}/items")
    public Result<List<DictItemOptionVO>> getDictItems(
            @Parameter(description = "字典编码") @PathVariable String dictCode
    ) {
        List<DictItemOptionVO> list = dictItemService.getDictItems(dictCode);
        return Result.success(list);
    }

    @Operation(summary = "新增字典项")
    @PostMapping("/{dictCode}/items")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:add')")
    @RepeatSubmit
    public Result<Void> saveDictItem(
            @PathVariable String dictCode,
            @Valid @RequestBody DictItemForm formData
    ) {
        formData.setDictCode(dictCode);
        boolean result = dictItemService.saveDictItem(formData);
        
        // 发送字典更新通知
        if (result) {
            webSocketMessageService.sendDictUpdatedEvent(dictCode);
        }
        
        return Result.judge(result);
    }

    @Operation(summary = "字典项表单数据")
    @GetMapping("/{dictCode}/items/{itemId}/form")
    public Result<DictItemForm> getDictItemForm(
            @PathVariable String dictCode,
            @Parameter(description = "字典项ID") @PathVariable Long itemId
    ) {
        DictItemForm formData = dictItemService.getDictItemForm(itemId);
        return Result.success(formData);
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/{dictCode}/items/{itemId}")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:edit')")
    @RepeatSubmit
    public Result<?> updateDictItem(
            @PathVariable String dictCode,
            @PathVariable Long itemId,
            @RequestBody DictItemForm formData
    ) {
        formData.setId(itemId);
        formData.setDictCode(dictCode);
        boolean status = dictItemService.updateDictItem(formData);
        
        // 发送字典更新通知
        if (status) {
            webSocketMessageService.sendDictUpdatedEvent(dictCode);
        }
        
        return Result.judge(status);
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/{dictCode}/items/{itemIds}")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:delete')")
    public Result<Void> deleteDictItems(
            @PathVariable String dictCode,
            @Parameter(description = "字典ID，多个以英文逗号(,)拼接") @PathVariable String itemIds
    ) {
        dictItemService.deleteDictItemByIds(itemIds);
        
        // 发送字典更新通知
        webSocketMessageService.sendDictUpdatedEvent(dictCode);
        
        return Result.success();
    }

}
