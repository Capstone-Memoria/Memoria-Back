package ac.mju.memoria.backend.domain.user.dto;

import ac.mju.memoria.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

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

        // Entity -> DTO 변환 메서드
        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .lastModifiedAt(user.getLastModifiedAt())
                    .build();
        }
    }

    // 사용자 정보 수정 요청용 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserUpdateRequest {
        private String nickName;
        private String password;
    }
}
