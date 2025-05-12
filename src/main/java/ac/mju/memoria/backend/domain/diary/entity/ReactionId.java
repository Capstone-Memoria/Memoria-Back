package ac.mju.memoria.backend.domain.diary.entity;

import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionId {
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Diary diary;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

    public static ReactionId of(Diary diary, User user) {
        return ReactionId.builder()
                .diary(diary)
                .user(user)
                .build();
    }
}
