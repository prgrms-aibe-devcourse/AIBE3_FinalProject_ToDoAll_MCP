package com.mcp.config;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ToolCallbackConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(List<ToolCallback> callbacks) {
        return () -> callbacks.toArray(new ToolCallback[0]);
    }
}