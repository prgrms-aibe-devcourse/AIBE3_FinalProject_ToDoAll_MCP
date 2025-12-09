package com.mcp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider toolCallbackProvider) {

        try {
            ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
            log.info("[MCP] 등록된 MCP Tool 개수 = {}", tools.length);
            for (ToolCallback tool : tools) {
                log.info("[MCP] MCP Tool = {}", tool);
            }
        } catch (Exception e) {
            log.error("[MCP] MCP Tool 조회 실패", e);
        }

        return builder
                .defaultTools(toolCallbackProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}