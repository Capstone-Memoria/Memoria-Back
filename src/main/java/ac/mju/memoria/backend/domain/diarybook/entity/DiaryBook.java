package ac.mju.memoria.backend.domain.diarybook.entity;

import java.util.ArrayList;
import java.util.List;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.AbstractSticker;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class DiaryBook extends UserStampedEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String spineColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "coverImageFile_id")
    private CoverImageFile coverImageFile;

    @OneToMany(mappedBy = "diaryBook")
    @Builder.Default
    private List<AbstractSticker> abstractStickers = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "diaryBook")
    @Builder.Default
    private List<DiaryBookMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL)
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AICharacter> characters = new ArrayList<>();

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserDiaryBookPin> userDiaryBookPins = new ArrayList<>();

    @OneToMany(mappedBy = "diaryBook", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DiaryBookStatistics> statistics = new ArrayList<>();

    public boolean isAdmin(User user) {
        if (owner.getEmail().equals(user.getEmail())) {
            return true;
        }

        return isMemberAdmin(user);
    }

    public boolean isMember(User user) {
        return members.stream().anyMatch(member -> member.getUser().getEmail().equals(user.getEmail()))
                || isAdmin(user);
    }

    private boolean isMemberAdmin(User user) {
        return members.stream()
                .anyMatch(
                        member -> member.getUser().getEmail().equals(user.getEmail())
                                && member.getPermission() == MemberPermission.ADMIN);
    }

    public void addDiary(Diary diary) {
        this.diaries.add(diary);
        diary.setDiaryBook(this);
    }

    public void addCharacter(AICharacter character) {
        this.characters.add(character);
        character.setDiaryBook(this);
    }
}
