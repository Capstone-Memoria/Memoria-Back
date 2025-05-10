package ac.mju.memoria.backend.domain.notification.event;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewDiaryEvent {
    private final Long diaryId;
}
