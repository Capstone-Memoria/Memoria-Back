package ac.mju.memoria.backend.domain.ai.llm.model;

import ac.mju.memoria.backend.domain.diarybook.entity.enums.EmotionWeather;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AIEmotionWeatherResponse {
  private EmotionWeather emotionWeather;
  private String reason;
}