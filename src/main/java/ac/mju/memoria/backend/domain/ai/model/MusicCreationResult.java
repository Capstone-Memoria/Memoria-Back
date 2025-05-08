package ac.mju.memoria.backend.domain.ai.model;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MusicCreationResult {
  private final Long diaryId;
  private final InputStream musicFileStream;
}