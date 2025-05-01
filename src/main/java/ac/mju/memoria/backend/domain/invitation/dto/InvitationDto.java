package ac.mju.memoria.backend.domain.invitation.dto;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.invitation.entity.CodeInvitation;
import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "초대 코드 생성 요청 DTO")
    public static class CreateCodeInviteRequest {
        @Schema(description = "초대 코드 유효 시간 (시간 단위, null이면 기본값 적용)", example = "24")
        private Integer hours;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "직접 초대 생성 요청 DTO")
    public static class CreateDirectInviteRequest {
        @NotBlank(message = "초대를 원하는 상대방의 이메일을 입력해 주세요.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Schema(description = "초대할 사용자의 이메일", example = "invited@example.com")
        private String targetEmail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "초대 코드 수락 요청 DTO")
    public static class AcceptCodeInviteRequest {
        @NotBlank(message = "초대코드를 입력하세요")
        @Schema(description = "수락할 초대 코드", example = "ABCDEF12")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "직접 초대 수락 요청 DTO")
    public static class AcceptDirectInviteRequest {
        @NotNull(message = "초대장 아이디를 입력하세요")
        @Schema(description = "수락할 직접 초대 ID", example = "1")
        private Long id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "직접 초대 생성 응답 DTO")
    public static class CreateDirectInviteResponse {
        @Schema(description = "생성된 직접 초대 ID")
        private Long id;
        @Schema(description = "초대된 다이어리 북 정보")
        private DiaryBookDto.DiaryBookResponse diaryBook;
        @Schema(description = "초대한 사용자 정보")
        private UserDto.UserResponse inviteBy;
        @Schema(description = "초대받은 사용자 정보")
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
    @Schema(description = "초대 코드 생성 응답 DTO")
    public static class CreateCodeInviteResponse {
        @Schema(description = "생성된 초대 코드 ID")
        private Long id;
        @Schema(description = "초대 코드가 생성된 다이어리 북 정보")
        private DiaryBookDto.DiaryBookResponse diaryBook;
        @Schema(description = "초대 코드를 생성한 사용자 정보")
        private UserDto.UserResponse inviteBy;
        @Schema(description = "생성된 초대 코드")
        private String inviteCode;
        @Schema(description = "초대 코드 만료 시간")
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
    @Schema(description = "초대 코드 상세 정보 응답 DTO")
    public static class CodeInviteDetailsResponse {
        @Schema(description = "초대 코드 ID")
        private Long id;
        @Schema(description = "초대 코드가 생성된 다이어리 북 정보")
        private DiaryBookDto.DiaryBookResponse diaryBook;
        @Schema(description = "초대 코드를 생성한 사용자 정보")
        private UserDto.UserResponse inviteBy;
        @Schema(description = "초대 코드")
        private String inviteCode;
        @Schema(description = "초대 코드 만료 시간")
        private LocalDateTime expiresAt;
        @Schema(description = "초대 코드 생성 시간")
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
