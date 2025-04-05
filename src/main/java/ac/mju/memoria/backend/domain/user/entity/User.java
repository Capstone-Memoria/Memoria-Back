package ac.mju.memoria.backend.domain.user.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
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
}
