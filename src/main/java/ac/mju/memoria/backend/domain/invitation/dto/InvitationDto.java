package ac.mju.memoria.backend.domain.invitation.dto;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.invitation.entity.CodeInvitation;
import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class InvitationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCodeInviteRequest {
        private Integer hours;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDirectInviteRequest {
        @NotBlank(message = "초대를 원하는 상대방의 이메일을 입력해 주세요.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String targetEmail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptCodeInviteRequest {
        @NotBlank(message = "초대코드를 입력하세요")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptDirectInviteRequest {
        @NotNull(message = "초대장 아이디를 입력하세요")
        private Long id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDirectInviteResponse {
        private Long id;
        private DiaryBookDto.DiaryBookResponse diaryBook;
        private UserDto.UserResponse inviteBy;
        private UserDto.UserResponse inviteTo;

        public static CreateDirectInviteResponse from(DirectInvitation entity) {
            return CreateDirectInviteResponse.builder()
                    .id(entity.getId())
                    .diaryBook(DiaryBookDto.DiaryBookResponse.from(entity.getDiaryBook()))
                    .inviteBy(UserDto.UserResponse.from(entity.getInviteBy()))
                    .inviteTo(UserDto.UserResponse.from(entity.getInviteTo()))
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCodeInviteResponse {
        private Long id;
        private DiaryBookDto.DiaryBookResponse diaryBook;
        private UserDto.UserResponse inviteBy;
        private String inviteCode;
        private LocalDateTime expiresAt;

        public static CreateCodeInviteResponse from(CodeInvitation entity) {
            return CreateCodeInviteResponse.builder()
                    .id(entity.getId())
                    .diaryBook(DiaryBookDto.DiaryBookResponse.from(entity.getDiaryBook()))
                    .inviteBy(UserDto.UserResponse.from(entity.getInviteBy()))
                    .inviteCode(entity.getInviteCode())
                    .expiresAt(entity.getExpiresAt())
                    .build();
        }
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CodeInviteDetailsResponse {
        private Long id;
        private DiaryBookDto.DiaryBookResponse diaryBook;
        private UserDto.UserResponse inviteBy;
        private String inviteCode;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;

        public static CodeInviteDetailsResponse from(CodeInvitation entity) {
            return CodeInviteDetailsResponse.builder()
                    .id(entity.getId())
                    .diaryBook(DiaryBookDto.DiaryBookResponse.from(entity.getDiaryBook()))
                    .inviteBy(UserDto.UserResponse.from(entity.getInviteBy()))
                    .inviteCode(entity.getInviteCode())
                    .expiresAt(entity.getExpiresAt())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}
