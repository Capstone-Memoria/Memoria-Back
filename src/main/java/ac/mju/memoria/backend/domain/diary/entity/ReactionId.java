package ac.mju.memoria.backend.domain.diary.entity;

import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionId {
  @ManyToOne
  private Diary diary;

  @ManyToOne
  private User user;

    public static ReactionId of(Diary diary, User user) {
        return ReactionId.builder()
                .diary(diary)
                .user(user)
                .build();
    }
}
