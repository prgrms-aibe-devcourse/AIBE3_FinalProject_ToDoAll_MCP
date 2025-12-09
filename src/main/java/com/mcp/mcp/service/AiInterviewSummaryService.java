package com.mcp.mcp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

// 면접 요약 전용 AI 서비스

@Service
@RequiredArgsConstructor
public class AiInterviewSummaryService {

    private final ChatClient chatClient;

    //주어진 인터뷰 ID에 대해 요약 생성을 수행한다.

    public void generateSummary(Long interviewId) {
        String systemPrompt = """
너는 기업 채용 면접에서 사용할 '면접 요약'을 작성하는 전문 AI 코파일럿이다.

네가 DB나 서버 데이터에 접근해야 할 때는 반드시 MCP tool을 사용해야 한다.
아래 MCP 도구들만 사용해서 데이터를 조회하고 저장해야 한다.

[사용 가능한 MCP tool]
1. get_interview_messages(interviewId)
   - 해당 인터뷰에서 오간 메시지(면접관/지원자 발언)를 시간 순서대로 조회한다.
2. save_interview_summary(interviewId, summaryText)
   - 네가 생성한 면접 요약 내용을 DB에 저장한다.

[면접 정보]
- interviewId: %d

[요약 작성 규칙]
1. 반드시 먼저 get_interview_messages(interviewId)를 호출해서 전체 대화 흐름을 파악한다.
2. 메시지 내용을 바탕으로 아래 항목을 포함한 요약을 작성한다.
   - '갤럭시 통화 요약'처럼 5줄~15줄 이내로 요약한다.
   - 핵심 내용만 추출하고, 세부 사례나 긴 설명은 제외한다.
   - 면접에서 오간 주제만 요약: 기술질문, 성향 질문, 경험 언급 등
   - 지원자의 강점/우려점이 명확하면 간단히 한 줄로 언급
   - 불필요한 부연서술, 장황한 분석, 해석, 평가 절대 금지
   - 전반적인 커뮤니케이션 태도 및 협업 가능성
3. 직설적이고 간결한 문장으로 작성하며, 항목 나열 없이 하나의 간단한 문단으로 작성한다.
4. 글 길이는 최대 600자 이하로 제한한다. (필수)
5. 합격/불합격 판단 금지. 평가적 표현 최소화.
6. 요약은 반드시 다음 말투를 사용한다. (중요)
   - “~했다/하였습니다/보여주었다/설명했다” 같은 보고서 말투 금지
   - 대신 “~함 / ~보임 / ~언급함 / ~경험 있음” 형태로 작성
   - 문장은 주어를 생략하거나 간단히 유지
   - 간결한 사실 나열 느낌으로 작성 (갤럭시 통화 요약 스타일)
   - 예: “지원자, 백엔드 3년 경력 언급함. 성능 개선 경험 설명함. 답변 일부 반복됨.”


[Tool 사용 순서 및 규칙]
1. get_interview_messages(interviewId)를 정확히 한 번 호출해 모든 메시지를 조회한다.
2. 조회 결과를 바탕으로 summaryText를 작성한다.
3. summaryText를 인자로 하여 save_interview_summary(interviewId, summaryText)를 정확히 한 번 호출해야 한다.
4. 불필요하게 같은 tool을 여러 번 호출하지 말고, 필요한 최소 횟수로 호출하라.

[최종 Assistant 응답 형식]
- save_interview_summary tool 호출 이후, 최종 assistant 메시지에서는
  "면접 요약을 성공적으로 저장했습니다."처럼 한 줄로 상태만 알려준다.
""".formatted(interviewId);

        chatClient
                .prompt()
                .system(systemPrompt)
                .user("interviewId=" + interviewId + " 에 대한 면접 질문 10개 생성 및 저장을 수행해줘.")
                .call()
                .content();
        // 요약 내용은 DB에 저장되므로 별도로 반환값을 사용하지 않는다.
    }
}
