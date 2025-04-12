package ac.mju.memoria.backend.domain.user.dto;

import ac.mju.memoria.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponse {
        private String nickName;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        public static UserResponse from(User user) {
            if (Objects.isNull(user)) {
                return null;
            }
            return UserResponse.builder()
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .lastModifiedAt(user.getLastModifiedAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserUpdateRequest {
        private String nickName;
        private String password;

        public void applyTo(User user, PasswordEncoder passwordEncoder) {
            if (Objects.nonNull(password)) {
                user.setPassword(passwordEncoder.encode(password));
            }
            if (Objects.nonNull(nickName)) {
                user.setNickName(nickName);
            }
        }
    }
}
