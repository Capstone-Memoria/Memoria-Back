package ac.mju.memoria.backend.domain.user.dto;

import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "사용자 정보 응답 DTO")
    public static class UserResponse {
        @Schema(description = "사용자 닉네임", example = "메모리아")
        private String nickName;
        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;
        @Schema(description = "계정 생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "마지막 정보 수정 시간")
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
    @Schema(description = "사용자 프로필 수정 요청 DTO")
    public static class UserUpdateRequest {
        @Schema(description = "변경할 닉네임", example = "새로운메모리아")
        private String nickName;
        @Schema(description = "현재 비밀번호 (비밀번호 변경 시 필수)", example = "currentPassword123")
        private String currentPassword;
        @Schema(description = "새 비밀번호 (비밀번호 변경 시 필수)", example = "newPassword456")
        private String newPassword;
        // 프로필 이미지 변경 필드는 추후 추가될 수 있음

        public void applyTo(User user, PasswordEncoder passwordEncoder) {
            if (Objects.nonNull(newPassword)) {
                if (Objects.isNull(currentPassword) || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                    throw new RestException(ErrorCode.USER_PASSWORD_NOT_MATCH);
                }
                user.setPassword(passwordEncoder.encode(newPassword));
            }
            if (Objects.nonNull(nickName)) {
                user.setNickName(nickName);
            }
        }
    }
}
