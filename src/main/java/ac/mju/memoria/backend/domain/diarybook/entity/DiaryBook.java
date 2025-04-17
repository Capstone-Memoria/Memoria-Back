package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DiaryBook extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private boolean isPinned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "diaryBook")
    @Builder.Default
    private List<DiaryBookMember> members = new ArrayList<>();

    public boolean isAdmin(User user) {
        if(owner.getEmail().equals(user.getEmail())) {
            return true;
        }

        return isMemberAdmin(user);
    }

    public boolean isMember(User user) {
        return members.stream().anyMatch(member -> member.getUser().getEmail().equals(user.getEmail()));
    }

    private boolean isMemberAdmin(User user) {
        return members.stream()
                .anyMatch(
                        member ->
                                member.getUser().getEmail().equals(user.getEmail()) && member.getPermission().equals(MemberPermission.ADMIN)
                );
    }

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();
}
