package com.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckTool {

    @Tool(description = "MCP health check tool")
    public String healthCheck() {
        return "OK";
    }
}