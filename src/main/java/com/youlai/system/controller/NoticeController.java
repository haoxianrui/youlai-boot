package com.youlai.system.controller;

import com.youlai.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.system.model.form.NoticeForm;
import com.youlai.system.model.query.NoticeQuery;
import com.youlai.system.model.vo.NoticeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.system.common.result.PageResult;
import com.youlai.system.common.result.Result;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * 通知公告前端控制层
 *
 * @author youlaitech
 * @since 2024-08-08 11:46
 */
@Tag(name = "通知公告接口")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController  {

    private final NoticeService noticeService;

    @Operation(summary = "通知公告分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:notice:query')")
    public PageResult<NoticeVO> getNoticePage(NoticeQuery queryParams ) {
        IPage<NoticeVO> result = noticeService.getNoticePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增通知公告")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:notice:add')")
    public Result saveNotice(@RequestBody @Valid NoticeForm formData ) {
        boolean result = noticeService.saveNotice(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取通知公告表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:notice:edit')")
    public Result<NoticeForm> getNoticeForm(
        @Parameter(description = "通知公告ID") @PathVariable Long id
    ) {
        NoticeForm formData = noticeService.getNoticeFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改通知公告")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:notice:edit')")
    public Result updateNotice(
            @Parameter(description = "通知公告ID") @PathVariable Long id,
            @RequestBody @Validated NoticeForm formData
    ) {
        boolean result = noticeService.updateNotice(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:notice:delete')")
    public Result deleteNotices(
        @Parameter(description = "通知公告ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = noticeService.deleteNotices(ids);
        return Result.judge(result);
    }
}
