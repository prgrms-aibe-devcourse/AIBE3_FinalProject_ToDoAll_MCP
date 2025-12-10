package com.mcp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class ChatClientConfig {
    static {
        log.info("[MCP] >>> ChatClientConfig 클래스 로딩됨 (static block)");
    }
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

        // 🔥 여기서 원격 MCP 서버에서 가져온 Tool 목록을 찍어보는 게 핵심
        ToolCallback[] callbacks = mcpToolCallbackProvider.getToolCallbacks();
        log.info("[MCP] 원격 MCP Tool 개수 = {}", callbacks.length);
        for (ToolCallback cb : callbacks) {
            log.info("[MCP] ToolCallback = {}", cb);
        }

        return builder
                .defaultToolCallbacks(callbacks)       // 🔥 이걸로 기본 Tool 세팅
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 프롬프트/툴 콜 로그 advisor
                .build();
    }
}