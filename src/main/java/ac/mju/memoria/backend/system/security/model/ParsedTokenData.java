package ac.mju.memoria.backend.system.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class ParsedTokenData {
    private final LocalDateTime expireAt;
    private final String subject;
}
