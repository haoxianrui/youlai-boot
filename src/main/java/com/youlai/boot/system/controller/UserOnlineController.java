package com.youlai.boot.system.controller;

import com.youlai.boot.core.web.Result;
import com.youlai.boot.system.service.UserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 在线用户控制器
 *
 * @author You Lai
 * @since 3.0.0
 */
@Tag(name = "13.在线用户接口")
@RestController
@RequestMapping("/api/v1/users/online")
@RequiredArgsConstructor
public class UserOnlineController {

    private final UserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    @Operation(summary = "获取在线用户列表")
    @GetMapping
    @PreAuthorize("@ss.hasPerm('sys:monitor:online')")
    public Result<List<UserOnlineService.UserOnlineDTO>> getOnlineUsers() {
        return Result.success(userOnlineService.getOnlineUsers());
    }

    /**
     * 获取在线用户统计信息
     *
     * @return 在线用户统计
     */
    @Operation(summary = "获取在线用户统计")
    @GetMapping("/stats")
    @PreAuthorize("@ss.hasPerm('sys:monitor:online')")
    public Result<Map<String, Object>> getOnlineStats() {
        return Result.success(Map.of(
                "count", userOnlineService.getOnlineUserCount()
        ));
    }
} 
