package ac.mju.memoria.backend.domain.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MusicCreationQueueItem {
    private final Long diaryId;
    private final String genre;
    private final String lyrics;
}
