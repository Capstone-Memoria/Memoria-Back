package ac.mju.memoria.backend.domain.ai.llm.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Description("다이어리에 대한 AI 캐릭터의 답변 편지")
public class AICommentResponse {
    @Description("AI 캐릭터의 답변 편지 제목")
    private String title;
    @Description("AI 캐릭터의 답변 편지 내용 (마크다운으로 작성)")
    private String content;
}
