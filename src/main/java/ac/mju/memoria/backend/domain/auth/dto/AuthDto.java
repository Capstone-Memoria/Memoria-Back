package ac.mju.memoria.backend.domain.auth.dto;

import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @Schema(description = "로그인 요청 DTO")
    public static class SignInRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "사용자 이메일", example = "test@example.com")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "사용자 비밀번호", example = "password123")
        private String password;

    }

    // 로그인 응답 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "로그인 응답 DTO")
    public static class SignInResponse {
        @Schema(description = "발급된 Access Token")
        private String accessToken;
        @Schema(description = "Access Token 만료 시간")
        private LocalDateTime accessTokenExpiresAt;
        @Schema(description = "로그인한 사용자 정보")
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
    @Schema(description = "회원가입 요청 DTO")
    public static class SignUpRequest {
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(description = "사용자 이메일", example = "newuser@example.com")
        private String email;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "사용자 닉네임", example = "새로운유저")
        private String nickName;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "사용자 비밀번호", example = "newpassword123")
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
    @Schema(description = "이메일 중복 확인 요청 DTO")
    public static class EmailExistRequest {
        @Schema(description = "확인할 이메일 주소", example = "check@example.com")
        private String email;
    }

    // 이메일 인증 확인 응답 DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "이메일 중복 확인 응답 DTO")
    public static class EmailExistResponse {
        @Schema(description = "이메일 존재 여부 (true/false)", example = "false")
        private String isExist; // Boolean → String

        public static EmailExistResponse from(boolean exists) {
            return new EmailExistResponse(Boolean.toString(exists));
        }
    }
}
