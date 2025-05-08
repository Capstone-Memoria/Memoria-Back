package ac.mju.memoria.backend.domain.ai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MusicCreationQueueItemTest {

  @Test
  void testQueueItemCreation() {
    // Given
    Long diaryId = 1L;
    String genre = "pop";
    String lyrics = "테스트 가사";

    // When
    MusicCreationQueueItem item = new MusicCreationQueueItem(diaryId, genre, lyrics);

    // Then
    assertEquals(diaryId, item.getDiaryId());
    assertEquals(genre, item.getGenre());
    assertEquals(lyrics, item.getLyrics());
  }
}