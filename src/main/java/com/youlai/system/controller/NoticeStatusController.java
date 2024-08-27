package com.youlai.system.controller;

import com.youlai.system.service.NoticeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youlai.system.model.form.NoticeStatusForm;
import com.youlai.system.model.query.NoticeStatusQuery;
import com.youlai.system.model.vo.NoticeStatusVO;
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
 * 用户公告状态前端控制层
 *
 * @author youlaitech
 * @since 2024-08-27 09:53
 */
@Tag(name = "用户公告状态接口")
@RestController
@RequestMapping("/api/v1/noticeStatuss")
@RequiredArgsConstructor
public class NoticeStatusController  {

    private final NoticeStatusService noticeStatusService;

    @Operation(summary = "用户公告状态分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('system:noticeStatus:query')")
    public PageResult<NoticeStatusVO> getNoticeStatusPage(NoticeStatusQuery queryParams ) {
        IPage<NoticeStatusVO> result = noticeStatusService.getNoticeStatusPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "新增用户公告状态")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('system:noticeStatus:add')")
    public Result saveNoticeStatus(@RequestBody @Valid NoticeStatusForm formData ) {
        boolean result = noticeStatusService.saveNoticeStatus(formData);
        return Result.judge(result);
    }

    @Operation(summary = "获取用户公告状态表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('system:noticeStatus:edit')")
    public Result<NoticeStatusForm> getNoticeStatusForm(
        @Parameter(description = "用户公告状态ID") @PathVariable Long id
    ) {
        NoticeStatusForm formData = noticeStatusService.getNoticeStatusFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "修改用户公告状态")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('system:noticeStatus:edit')")
    public Result updateNoticeStatus(
            @Parameter(description = "用户公告状态ID") @PathVariable Long id,
            @RequestBody @Validated NoticeStatusForm formData
    ) {
        boolean result = noticeStatusService.updateNoticeStatus(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "删除用户公告状态")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('system:noticeStatus:delete')")
    public Result deleteNoticeStatuss(
        @Parameter(description = "用户公告状态ID，多个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean result = noticeStatusService.deleteNoticeStatuss(ids);
        return Result.judge(result);
    }
}
