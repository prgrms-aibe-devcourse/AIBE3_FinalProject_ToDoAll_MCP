package com.mcp.mcp.controller;

import com.mcp.mcp.service.AiInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/interviews")
public class AiInterviewController {

    private final AiInterviewService aiInterviewService;

    @PostMapping("/generate-questions")
    public ResponseEntity<Void> generate(@RequestBody InterviewGenerateRequest dto) {
        aiInterviewService.generateQuestions(dto.interviewId());
        return ResponseEntity.accepted().build();
    }

    public record InterviewGenerateRequest(Long interviewId) {}
}