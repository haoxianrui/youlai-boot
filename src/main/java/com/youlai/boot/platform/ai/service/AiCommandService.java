package com.youlai.boot.platform.ai.service;

import com.youlai.boot.platform.ai.model.dto.AiExecuteRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AI 命令编排服务：负责对外的解析与执行编排
 */
public interface AiCommandService {

	/**
	 * 解析自然语言命令
	 */
	AiParseResponseDTO parseCommand(AiParseRequestDTO request, HttpServletRequest httpRequest);

	/**
	 * 执行已解析的命令
	 * 
	 * @param request 执行请求
	 * @param httpRequest HTTP 请求
	 * @return 执行结果数据（成功时返回）
	 * @throws Exception 执行失败时抛出异常
	 */
	Object executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest) throws Exception;
}





