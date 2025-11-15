package com.youlai.boot.platform.ai.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlai.boot.platform.ai.config.AiProperties;
import com.youlai.boot.platform.ai.model.dto.*;
import com.youlai.boot.platform.ai.model.entity.AiCommandAudit;
import com.youlai.boot.platform.ai.provider.AiProvider;
import com.youlai.boot.platform.ai.provider.AiProviderFactory;
import com.youlai.boot.platform.ai.service.AiCommandService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI 命令服务实现类（重构版）
 * 
 * 重构改进：
 * 1. ✅ 使用策略模式 + 工厂模式管理提供商，消除 switch-case
 * 2. ✅ 配置映射化，添加新提供商只需配置，无需修改代码
 * 3. ✅ 统一命名为 base-url，符合行业惯例
 * 4. ✅ Service 层直接返回 DTO，不包装 Result（由 Controller 统一处理）
 * 5. ✅ 职责清晰，扩展性强
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiCommandServiceImpl implements AiCommandService {

    private final AiProperties aiProperties;
    private final AiProviderFactory providerFactory;

    // 审计日志存储（简化实现，实际应使用数据库）
    private final Map<String, AiCommandAudit> auditStore = new HashMap<>();

    /**
     * 解析自然语言命令
     * 
     * 注意：直接返回 DTO，不包装 Result
     * Controller 负责统一包装成 Result
     */
    @Override
    public AiCommandResponseDTO parseCommand(AiCommandRequestDTO request, HttpServletRequest httpRequest) {
        // 检查 AI 功能是否启用
        if (!aiProperties.getEnabled()) {
            throw new IllegalStateException("AI 功能未启用，请在配置文件中设置 ai.enabled=true");
        }

        try {
            // 获取当前提供商（自动校验配置）
            AiProvider provider = providerFactory.getCurrentProvider();
            
            log.info("📤 使用 {} 解析命令: {}", provider.getProviderName(), request.getCommand());

            // 构建提示词
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(request);

            // 调用 AI API
            String response = provider.call(systemPrompt, userPrompt);

            // 解析响应
            return parseAiResponse(response);

        } catch (IllegalStateException e) {
            // 配置错误，抛出让 Controller 处理
            throw e;
        } catch (Exception e) {
            log.error("解析命令失败", e);
            throw new RuntimeException("解析命令失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行已解析的命令
     */
    @Override
    public AiExecuteResponseDTO executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest) {
        // TODO: 实现命令执行逻辑
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 获取命令执行历史
     */
    @Override
    public Map<String, Object> getCommandHistory(Integer page, Integer size) {
        List<AiCommandAudit> allAudits = new ArrayList<>(auditStore.values());
        allAudits.sort(Comparator.comparing(AiCommandAudit::getCreateTime).reversed());

        int total = allAudits.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);

        List<AiCommandAudit> pageData = start < total ? allAudits.subList(start, end) : new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    /**
     * 获取可用的函数列表
     */
    @Override
    public List<Map<String, Object>> getAvailableFunctions() {
        List<Map<String, Object>> functions = new ArrayList<>();

        // 用户管理函数
        functions.add(createFunctionDef(
                "deleteUser",
                "删除用户",
                Map.of("name", "String - 用户姓名", "id", "Long - 用户ID（可选）")
        ));

        functions.add(createFunctionDef(
                "updateUser",
                "更新用户信息",
                Map.of("id", "Long - 用户ID", "nickname", "String - 昵称", "status", "Integer - 状态")
        ));

        functions.add(createFunctionDef(
                "queryUsers",
                "查询用户列表",
                Map.of("name", "String - 姓名（可选）", "status", "Integer - 状态（可选）")
        ));

        // 角色管理函数
        functions.add(createFunctionDef(
                "assignRole",
                "分配角色给用户",
                Map.of("userId", "Long - 用户ID", "roleIds", "List<Long> - 角色ID列表")
        ));

        return functions;
    }

    /**
     * 撤销命令执行
     */
    @Override
    public void rollbackCommand(String auditId) {
        AiCommandAudit audit = auditStore.get(auditId);
        if (audit == null) {
            throw new RuntimeException("审计记录不存在");
        }

        if (!"success".equals(audit.getExecuteStatus())) {
            throw new RuntimeException("只能撤销成功执行的命令");
        }

        // TODO: 实现具体的回滚逻辑
        log.info("撤销命令执行: auditId={}, function={}", auditId, audit.getFunctionName());
        throw new UnsupportedOperationException("回滚功能尚未实现");
    }

    // ==================== 私有方法 ====================

    /**
     * 构建系统提示词（包含可用函数定义）
     */
    private String buildSystemPrompt() {
        return """
                你是一个专业的命令解析助手。你的任务是将用户的自然语言命令转换为结构化的函数调用。
                
                可用函数：
                1. queryUsers - 查询用户列表
                   参数：keywords(搜索关键字), status(状态), deptId(部门ID)
                   
                2. deleteUser - 删除用户
                   参数：userId(用户ID)
                   
                3. updateUser - 更新用户信息
                   参数：userId(用户ID), nickname(昵称), mobile(手机号)
                
                请将命令解析为以下 JSON 格式：
                {
                  "functionCalls": [
                    {
                      "function": "函数名",
                      "parameters": { "参数名": "参数值" },
                      "description": "操作说明"
                    }
                  ]
                }
                """;
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(AiCommandRequestDTO request) {
        return "请解析以下命令：" + request.getCommand();
    }

    /**
     * 解析 AI 响应
     */
    private AiCommandResponseDTO parseAiResponse(String response) {
        try {
            // 提取 JSON
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}") + 1;

            if (jsonStart == -1 || jsonEnd == 0) {
                throw new IllegalArgumentException("AI 返回格式错误：未找到 JSON");
            }

            String jsonStr = response.substring(jsonStart, jsonEnd);
            JSONObject json = JSONUtil.parseObj(jsonStr);

            // 解析函数调用列表
            List<FunctionCallDTO> functionCalls = new ArrayList<>();
            JSONArray callsArray = json.getJSONArray("functionCalls");

            if (callsArray != null) {
                for (int i = 0; i < callsArray.size(); i++) {
                    JSONObject call = callsArray.getJSONObject(i);
                    functionCalls.add(FunctionCallDTO.builder()
                            .name(call.getStr("function"))
                            .arguments(call.getJSONObject("parameters") != null ? 
                                call.getJSONObject("parameters").toBean(Map.class) : new HashMap<>())
                            .description(call.getStr("description"))
                            .build());
                }
            }

            return AiCommandResponseDTO.builder()
                    .success(true)
                    .functionCalls(functionCalls)
                    .rawResponse(response)
                    .build();

        } catch (Exception e) {
            log.error("解析 AI 响应失败", e);
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建函数定义
     */
    private Map<String, Object> createFunctionDef(String name, String description, Map<String, String> parameters) {
        Map<String, Object> func = new HashMap<>();
        func.put("name", name);
        func.put("description", description);
        func.put("parameters", parameters);
        return func;
    }
}

