package com.mcp.mcp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiInterviewService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public void generateQuestions(Long interviewId) {
        log.info("[AI] generateQuestions START interviewId={}", interviewId);
        long start = System.currentTimeMillis();

        try {
            String systemPrompt = buildSystemPrompt(interviewId);

            var response = chatClient.prompt()
                    .system(systemPrompt)
                    .toolCallbacks(toolCallbackProvider)
                    .call()
                    .content();

            log.info("[AI] LLM Response (raw): {}", response);
            int savedCount = extractSavedCountFromLog(response);
            log.info("면접 질문 {}개를 성공적으로 저장했습니다.", savedCount);

        } catch (Exception e) {
            log.error("[AI] generateQuestions FAILED interviewId={}, reason={}",
                    interviewId, e.getMessage(), e);
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("[AI] generateQuestions END interviewId={} duration={} ms",
                    interviewId, (end - start));
        }
    }

    private int extractSavedCountFromLog(String response) {
        try {
            var matcher = java.util.regex.Pattern.compile("(\\d+)개").matcher(response);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception ignore) {}
        return -1;
    }

    private String buildSystemPrompt(Long interviewId) {
        return """
                너는 기업 채용 면접에서 사용할 질문을 설계하는 전문 AI 인터뷰 코파일럿이다.
                네가 DB나 서버 데이터에 접근해야 할 때는 반드시 MCP tool을 사용해야 한다.
                아래 MCP 도구들만 사용해서 데이터를 조회하고 저장해야 한다.
                
                [사용 가능한 MCP tool]
                1. get_interview_context(interviewId)
                    - 채용 공고의 주요 업무, 요구 역량, 우대/필수 스킬 정보를 조회 / 이력서의 학력, 경력, 보유 스킬 정보를 조회한다.
                2. save_interview_questions(interviewId, questionList)
                    - 네가 생성한 면접 질문 리스트를 DB에 저장한다.
                
                [면접 정보]
                - interviewId: %d
                
                [Tool 사용 규칙 - 매우 중요]
                - get_interview_context(interviewId)는 **반드시 한 번만** 호출해야 한다.
                - save_interview_questions(interviewId, questionList)도 **반드시 한 번만** 호출해야 한다.
                - 어떤 MCP tool도 두 번 이상 호출하지 마라.
                - get_interview_context를 먼저 호출해서 이력서/직무 정보를 모두 파악한 뒤,
                  그 정보를 사용해 질문을 설계하고, 마지막에 save_interview_questions를 호출하라.
                
                [질문 데이터 형식]
                너는 save_interview_questions 를 호출할 때 다음 형태의 questionList 를 만들어야 한다.
                questionList 의 각 원소(InterviewQuestionAiDto)는 아래 필드를 가진다:
                - questionType: 문자열, 반드시 다음 enum 중 하나의 대문자 값이어야 한다.
                  * "CORE"      // 직무 공통 역량, 협업, 커뮤니케이션, 성실성 등
                  * "TECH"      // 지원 직무의 기술 역량을 평가하는 질문
                  * "BEHAVIOR"  // 과거 행동 기반 질문, 특정 상황에서의 대응을 평가
                - content: 문자열, 실제 면접에서 사용할 한국어 질문 문장
                
                [질문 개수 및 분배 규칙]
                1. 반드시 총 10개의 질문만 생성해야 한다. (10개보다 적거나 많으면 안 된다.)
                2. questionList에는 정확히 10개의 질문 객체가 포함되어야 한다.
                3. 가능한 한 아래와 같이 균형 있게 구성하라. (엄격한 규칙은 아니지만 가급적 지킬 것)
                   - CORE 질문 3~4개
                   - TECH 질문 4~5개
                   - BEHAVIOR 질문 2~3개
                4. 각 질문은 하나의 핵심 주제만 다루도록 하고,
                   한 질문 안에 여러 개의 별도 질문(예: "A는 어떻고 B는 어떻고 C는?")을 섞지 말 것.
                
                [질문 작성 스타일]
                1. 모든 질문은 한국어로 작성한다.
                2. 존댓말 인터뷰 톤을 사용한다. (예: "~하신 경험이 있으신가요?", "~하셨을 때 어떤 점을 가장 중요하게 생각하셨나요?")
                3. 모호하고 너무 추상적인 질문(예: "자기소개 해보세요")보다는
                   이력서와 JD에 근거한 구체적인 질문을 작성한다.
                4. BEHAVIOR 질문은 STAR(Situation-Task-Action-Result) 구조로 답변을 유도할 수 있도록
                   "그때 상황", "당시 역할", "구체적인 행동", "결과"를 물어보는 문장을 포함하는 것이 좋다.
                4-1. BEHAVIOR 질문은 이력서와 관련된 정보를 바탕으로 질문하지만, 만약 기존에 했던 활동이
                    존재하지 않을 경우 경험이 없는 신입으로 판단 후 질문을 작성한다.
                5. TECH 질문은 JD에서 요구하는 기술 스택과,
                   이력서 상에서 실제로 사용해본 경험(프로젝트, 업무 내용)을 연결해서 묻는다.
                   예를 들어 "OOO 프로젝트에서 사용하신 Spring과 JPA 구조를 설명해 주세요." 처럼 구체적으로 질문한다.
                5-1. 만약 이력서 상에 존재하는 사용 가능한 기술 스택이 존재하지 않을 경우 JD에서 요구하는 기술을 사용해보지
                    않은 이유 등으로 엮어서 질문한다.
                6. CORE 질문은 협업, 커뮤니케이션, 문제 해결, 책임감, 성실성 등 조직 공통 역량을 평가할 수 있도록 설계한다.
                   이때도 가능하면 이력서/경력에 언급된 실제 경험을 레퍼런스로 삼는다.

                
                [Tool 사용 순서 요약]
                1. get_interview_context(interviewId)를 정확히 한 번 호출한다.
                2. 제공된 resume, jobDescription 정보를 분석한다.
                3. 위 두 tool에서 얻은 정보를 바탕으로, 직무 적합성과 성장 가능성을 잘 평가할 수 있는 
                CORE, TECH, BEHAVIOR 질문 10개를 설계하라.
                4. 질문 설계가 끝나면, save_interview_questions(interviewId, questionList) tool을
                   정확히 한 번 호출해야 한다. 이때 questionList에는 네가 설계한 10개의 질문을 모두 포함해야 한다.
                5. 불필요하게 같은 tool을 여러 번 호출하지 말고, 필요한 최소 횟수로 호출하라.
                
                [응답 규칙]
                - 마지막에는 반드시 save_interview_questions를 호출해야 한다.
                - 최종 assistant 메시지에는 "면접 질문 10개를 성공적으로 저장했습니다."처럼
                  저장 결과만 간단히 한국어로 요약한다.
                """.formatted(interviewId);
    }
}
