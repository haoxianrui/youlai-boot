package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.common.result.PageResult;
import com.youlai.boot.common.result.Result;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.query.NoticeQuery;
import com.youlai.boot.system.model.vo.NoticeVO;
import com.youlai.boot.system.service.NoticeService;
import com.youlai.boot.system.service.NoticeStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告前端控制层
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Tag(name = "通知公告接口")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController  {

    private final NoticeService noticeService;

    private final NoticeStatusService noticeStatusService;

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
    public Result<?> saveNotice(@RequestBody @Valid NoticeForm formData ) {
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
    @Operation(summary = "管理页面查看通知公告")
    @GetMapping("/detail/{id}")
    public Result<?> getReadNoticeDetail(
            @Parameter(description = "通知公告ID")@PathVariable Long id) {
        return Result.success(noticeService.getReadNoticeDetail(id));
    }

    @Operation(summary = "修改通知公告")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:notice:edit')")
    public Result<?> updateNotice(
            @Parameter(description = "通知公告ID") @PathVariable Long id,
            @RequestBody @Validated NoticeForm formData
    ) {
        boolean result = noticeService.updateNotice(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "发布通知公告")
    @PatchMapping(value = "/release/{id}")
    @PreAuthorize("@ss.hasPerm('system:notice:release')")
    public Result<?> releaseNotice(@Parameter(description = "通知公告ID") @PathVariable Long id) {
        boolean result = noticeService.releaseNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "撤回通知公告")
    @PatchMapping(value = "/recall/{id}")
    @PreAuthorize("@ss.hasPerm('system:notice:recall')")
    public Result<?> recallNotice(@Parameter(description = "通知公告ID") @PathVariable Long id) {
        boolean result = noticeService.recallNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:notice:delete')")
    public Result<?> deleteNotices(
        @Parameter(description = "通知公告ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = noticeService.deleteNotices(ids);
        return Result.judge(result);
    }

    @Operation(summary = "获取未读的通知公告")
    @GetMapping("/unread")
    public Result<?> listUnreadNotices() {
        return Result.success(noticeStatusService.listUnreadNotices());
    }

    @Operation(summary = "阅读通知公告")
    @PatchMapping("/read/{id}")
    public Result<?> readNotice(@PathVariable Long id) {
        return Result.success(noticeService.readNotice(id));
    }

    @Operation(summary = "全部已读")
    @PatchMapping("/readAll")
    public Result<?> readAll() {
        noticeStatusService.readAll();
        return Result.success();
    }

    @Operation(summary = "获取我的通知公告")
    @GetMapping("/my/page")
    public PageResult<NoticeVO> getMyNoticePage(NoticeQuery queryParams) {
        IPage<NoticeVO> result = noticeService.getMyNoticePage(queryParams);
        return PageResult.success(result);
    }
}
