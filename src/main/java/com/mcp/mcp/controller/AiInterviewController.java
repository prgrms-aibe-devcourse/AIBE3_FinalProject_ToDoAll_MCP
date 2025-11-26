package com.mcp.mcp.controller;

import com.mcp.mcp.service.AiInterviewService;
import com.mcp.mcp.service.AiInterviewSummaryService;
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
    private final AiInterviewSummaryService aiInterviewSummaryService;


    @PostMapping("/generate-questions")
    public ResponseEntity<Void> generate(@RequestBody InterviewGenerateRequest dto) {
        aiInterviewService.generateQuestions(dto.interviewId(), dto.resumeId(), dto.jdId());
        return ResponseEntity.accepted().build();
    }
    //면접 요약 생성 요청 API
    @PostMapping("/generate-summary")
    public ResponseEntity<Void> generateSummary(@RequestBody InterviewSummaryRequest dto) {
        aiInterviewSummaryService.generateSummary(dto.interviewId());
        return ResponseEntity.accepted().build();
    }

    public record InterviewGenerateRequest(Long interviewId, Long resumeId, Long jdId) {}
    public record InterviewSummaryRequest(Long interviewId) {}

}