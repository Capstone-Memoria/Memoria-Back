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
@Description("가사 생성 요청에 대한 응답")
public class LyricsResponse {
    @Description("가사 생성 요청에 대한 내용 ([verse]로 시작)")
    private String lyrics;
}
