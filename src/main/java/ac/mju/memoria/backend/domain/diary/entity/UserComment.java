package ac.mju.memoria.backend.domain.diary.entity;

import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("USER")
public class UserComment extends Comment {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creater_email")
    private User createdBy;

    public static UserComment of(Diary diary, User createdBy) {
        return UserComment.builder()
                .diary(diary)
                .createdBy(createdBy)
                .build();
    }
}
