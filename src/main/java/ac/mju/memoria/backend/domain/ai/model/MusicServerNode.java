package ac.mju.memoria.backend.domain.ai.model;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Data
public class MusicServerNode {
    private final String host;

    private boolean busy = false;
    private String activeJobId;
    private Long relatedDiaryId;

    public String getURL() {
        return host;
    }
}
