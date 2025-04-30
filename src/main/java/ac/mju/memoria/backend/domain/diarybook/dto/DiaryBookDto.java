package ac.mju.memoria.backend.domain.diarybook.dto;


import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DiaryBookDto {

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class DiaryBookCreateRequest {
        @NotBlank(message = "제목을 입력해 주세요")
        private String title;
//        @NotNull(message = "커버이미지를 업로드해 주세요") TODO: 추후 활성화
        private MultipartFile coverImage;

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
        private MultipartFile coverImage;

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
        private FileDto.FileResponse coverImage;
        private List<StickerDto.StickerResponse> stickers;
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
                    .coverImage(FileDto.FileResponse.from(diaryBook.getCoverImageFile()))
                    .stickers(
                            Optional.ofNullable(diaryBook.getStickers())
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .map(StickerDto.StickerResponse::from)
                                    .toList()
                    )
                    .memberCount(diaryBook.getMembers() == null ? 1 : diaryBook.getMembers().size() + 1)
                    .build();
        }
    }
}
