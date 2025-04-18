package ac.mju.memoria.backend.domain.diarybook.dto;


import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

public class DiaryBookDto {

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class DiaryBookCreateRequest {
        @NotBlank(message = "제목을 입력해 주세요")
        private String title;

        public DiaryBook toEntity(User user) {
            return DiaryBook.builder()
                    .title(title)
                    .owner(user)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class DiaryBookUpdateRequest {
        private String title;
        private Boolean isPinned;

        public void applyTo(DiaryBook diaryBook) {
            if(Objects.nonNull(title)){
                diaryBook.setTitle(title);
            }
            if (Objects.nonNull(isPinned)) {
                diaryBook.setPinned(isPinned);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class DiaryBookResponse {
        private Long id;
        private String title;
        private Boolean isPinned;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private UserDto.UserResponse createdBy;
        private UserDto.UserResponse lastModifiedBy;
        private UserDto.UserResponse owner;
        private Integer memberCount;

        public static DiaryBookResponse from(DiaryBook diaryBook) {
            return DiaryBookResponse.builder()
                    .id(diaryBook.getId())
                    .title(diaryBook.getTitle())
                    .isPinned(diaryBook.isPinned())
                    .createdAt(diaryBook.getCreatedAt())
                    .lastModifiedAt(diaryBook.getLastModifiedAt())
                    .createdBy(UserDto.UserResponse.from(diaryBook.getCreatedBy()))
                    .lastModifiedBy(UserDto.UserResponse.from(diaryBook.getLastModifiedBy()))
                    .owner(UserDto.UserResponse.from(diaryBook.getOwner()))
                    .memberCount(diaryBook.getMembers() == null ? 1 : diaryBook.getMembers().size() + 1)
                    .build();
        }
    }

}
