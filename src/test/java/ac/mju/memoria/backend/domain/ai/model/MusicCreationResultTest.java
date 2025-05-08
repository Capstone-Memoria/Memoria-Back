package ac.mju.memoria.backend.domain.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class MusicCreationResultTest {

  @Test
  void testResultCreation() {
    // Given
    Long diaryId = 1L;
    byte[] musicData = "test music data".getBytes();
    InputStream musicStream = new ByteArrayInputStream(musicData);

    // When
    MusicCreationResult result = new MusicCreationResult(diaryId, musicStream);

    // Then
    assertEquals(diaryId, result.getDiaryId());
    assertNotNull(result.getMusicFileStream());
  }
}