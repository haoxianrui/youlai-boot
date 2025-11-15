package com.youlai.boot.system.controller;

import com.youlai.boot.common.result.Result;
import com.youlai.boot.system.service.WebSocketMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket测试控制器
 *
 * @author You Lai
 * @since 3.0.0
 */
@Tag(name = "12.WebSocket接口")
@RestController
@RequestMapping("/api/v1/websocket")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final WebSocketMessageService webSocketMessageService;

    /**
     * 发送字典更新事件
     *
     * @param dictCode 字典编码
     * @return 操作结果
     */
    @Operation(summary = "发送字典更新事件")
    @PostMapping("/dict/{dictCode}/updated")
    public Result<Void> sendDictUpdatedEvent(
            @Parameter(description = "字典编码") @PathVariable String dictCode
    ) {
        webSocketMessageService.sendDictUpdatedEvent(dictCode);
        return Result.success();
    }

    /**
     * 发送字典删除事件
     *
     * @param dictCode 字典编码
     * @return 操作结果
     */
    @Operation(summary = "发送字典删除事件")
    @PostMapping("/dict/{dictCode}/deleted")
    public Result<Void> sendDictDeletedEvent(
            @Parameter(description = "字典编码") @PathVariable String dictCode
    ) {
        webSocketMessageService.sendDictDeletedEvent(dictCode);
        return Result.success();
    }
} 