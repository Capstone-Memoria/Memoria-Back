package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DiaryBookMemberDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class MemberResponse {
        private Long id;

        private Long diaryBookId;

        private UserDto.UserResponse user;

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
