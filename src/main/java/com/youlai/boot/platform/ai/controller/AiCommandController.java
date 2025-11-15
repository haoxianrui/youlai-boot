package com.youlai.boot.platform.ai.controller;

import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.ai.model.dto.*;
import com.youlai.boot.platform.ai.service.AiCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * AI 命令控制器
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Tag(name = "AI命令接口")
@RestController
@RequestMapping("/api/v1/ai/command")
@RequiredArgsConstructor
@Slf4j
public class AiCommandController {

    private final AiCommandService aiCommandService;

    @Operation(summary = "解析自然语言命令")
    @PostMapping("/parse")
    public Result<AiCommandResponseDTO> parseCommand(
            @RequestBody AiCommandRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        log.info("收到AI命令解析请求: {}", request.getCommand());

        try {
            AiCommandResponseDTO response = aiCommandService.parseCommand(request, httpRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("命令解析失败", e);
            return Result.success(AiCommandResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @Operation(summary = "执行已解析的命令")
    @PostMapping("/execute")
    public Result<AiExecuteResponseDTO> executeCommand(
            @RequestBody AiExecuteRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        log.info("收到AI命令执行请求: {}", request.getFunctionCall().getName());

        try {
            AiExecuteResponseDTO response = aiCommandService.executeCommand(request, httpRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("命令执行失败", e);
            return Result.success(AiExecuteResponseDTO.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @Operation(summary = "获取命令执行历史")
    @GetMapping("/history")
    public Result<?> getCommandHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(aiCommandService.getCommandHistory(page, size));
    }

    @Operation(summary = "获取可用的函数列表")
    @GetMapping("/functions")
    public Result<?> getAvailableFunctions() {
        return Result.success(aiCommandService.getAvailableFunctions());
    }

    @Operation(summary = "撤销命令执行")
    @PostMapping("/rollback/{auditId}")
    public Result<?> rollbackCommand(
            @Parameter(description = "审计ID") @PathVariable String auditId
    ) {
        aiCommandService.rollbackCommand(auditId);
        return Result.success("撤销成功");
    }
}






