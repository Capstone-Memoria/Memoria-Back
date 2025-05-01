package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DiaryBookMemberDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Schema(description = "다이어리 북 멤버 응답 DTO")
    public static class MemberResponse {
        @Schema(description = "다이어리 북 멤버 ID")
        private Long id;

        @Schema(description = "다이어리 북 ID")
        private Long diaryBookId;

        @Schema(description = "멤버의 사용자 정보")
        private UserDto.UserResponse user;

        @Schema(description = "멤버 권한")
        private MemberPermission permission;

        public static MemberResponse from(DiaryBookMember member) {
            return MemberResponse.builder()
                    .id(member.getId())
                    .diaryBookId(member.getDiaryBook().getId())
                    .user(UserDto.UserResponse.from(member.getUser()))
                    .permission(member.getPermission())
                    .build();
        }
    }
}
