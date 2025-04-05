package ac.mju.memoria.backend.domain.user.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.group.entity.Group;
import ac.mju.memoria.backend.domain.usergroup.entity.UserGroup;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
@Table(name = "users")
public class User extends TimeStampedEntity {

    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @NotBlank(message = "닉네임을 입력해야 합니다.")
    private String nickName;

    @NotBlank(message = "이메일을 입력해야 합니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "creator")
    private List<Group> createdGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserGroup> userGroups = new ArrayList<>();

    public void addGroup(Group group) {
        UserGroup userGroup = new UserGroup(this, group);

        boolean isAlreadyJoined =
                this.userGroups.stream().anyMatch(ug -> ug.equals(userGroup));

        if (isAlreadyJoined) {
            throw new RestException(ErrorCode.USER_ALREADY_JOINED);
        }

        this.userGroups.add(userGroup);
        group.getUserGroups().add(userGroup);
    }
}
