package ac.mju.memoria.backend.domain.ai.llm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AISummaryResponse {
  private String oneLineSummary;
  private String longSummary;
}