package ac.mju.memoria.backend.domain.auth.dto;

import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class AuthDto {

    // 로그인 요청 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignInRequest {
        private String email;
        private String password;
    }

    // 로그인 응답 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignInResponse {
        private String accessToken;
        private LocalDateTime accessTokenExpiresAt;
        private UserDto.UserResponse user;

        public static SignInResponse of(
                String accessToken,
                UserDto.UserResponse user,
                LocalDateTime accessTokenExpiresAt
        ) {
            return SignInResponse.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiresAt(accessTokenExpiresAt)
                    .user(user)
                    .build();
        }
    }

    // 회원가입 요청 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignUpRequest {
        private String email;
        private String nickName;
        private String password;

        public User toEntity(PasswordEncoder encoder) {
            return User.builder()
                    .email(email)
                    .nickName(nickName)
                    .password(encoder.encode(password))
                    .build();
        }
    }


    // 이메일 인증 확인 요청 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EmailExistRequest {
        private String email;
    }

    // 이메일 인증 확인 응답 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EmailExistResponse {
        private String isExist; // Boolean → String

        public static EmailExistResponse from(boolean exists) {
            return new EmailExistResponse(Boolean.toString(exists));
        }
    }
}
