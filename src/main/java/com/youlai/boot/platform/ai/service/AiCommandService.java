package com.youlai.boot.platform.ai.service;

import com.youlai.boot.platform.ai.model.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * AI 命令服务接口
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
public interface AiCommandService {

    /**
     * 解析自然语言命令
     *
     * @param request     命令请求
     * @param httpRequest HTTP 请求
     * @return 解析结果
     */
    AiCommandResponseDTO parseCommand(AiCommandRequestDTO request, HttpServletRequest httpRequest);

    /**
     * 执行已解析的命令
     *
     * @param request     执行请求
     * @param httpRequest HTTP 请求
     * @return 执行结果
     */
    AiExecuteResponseDTO executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest);

    /**
     * 获取命令执行历史
     *
     * @param page 页码
     * @param size 每页数量
     * @return 历史记录
     */
    Map<String, Object> getCommandHistory(Integer page, Integer size);

    /**
     * 获取可用的函数列表
     *
     * @return 函数列表
     */
    List<Map<String, Object>> getAvailableFunctions();

    /**
     * 撤销命令执行
     *
     * @param auditId 审计ID
     */
    void rollbackCommand(String auditId);
}






