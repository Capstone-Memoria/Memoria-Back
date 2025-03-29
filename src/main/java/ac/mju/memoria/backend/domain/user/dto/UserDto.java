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
    public static class Response {
        private String nickName;
        private String email;
        private LocalDateTime createdAt;

        // Entity -> DTO 변환 메서드
        public static Response from(User user) {
            return Response.builder()
                    .nickName(user.getNickName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt()) // LocalDateTime -> String으로 변환
                    .build();
        }


        // 사용자 정보 수정 요청용 DTO
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Request {
            private String nickName;
            private String email;
            private String password;

            public User toEntity(PasswordEncoder encoder) {
                return User.builder()
                        .nickName(nickName)
                        .email(email)
                        .password(encoder.encode(password)) // 비밀번호 인코딩
                        .build();
            }

        }
    }
}