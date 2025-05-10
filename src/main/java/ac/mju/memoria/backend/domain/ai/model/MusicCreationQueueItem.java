package ac.mju.memoria.backend.domain.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MusicCreationQueueItem {
    private final Long diaryId;
}
