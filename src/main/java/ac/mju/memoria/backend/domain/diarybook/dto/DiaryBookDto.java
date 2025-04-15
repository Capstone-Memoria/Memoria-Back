package ac.mju.memoria.backend.domain.diarybook.dto;


import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.dto.StickerDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class DiaryBookDto {

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class DiaryBookCreateRequest {
        @NotBlank(message = "제목을 입력해 주세요")
        private String title;
        @NotNull(message = "커버이미지를 업로드해 주세요")
        private MultipartFile coverImage;

        public DiaryBook toEntity() {
            return DiaryBook.builder()
                    .title(title)
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
        private LocalDateTime createAt;
        private LocalDateTime lastModified;
        private UserDto.UserResponse createdBy;
        private UserDto.UserResponse lastModifiedBy;
        private UserDto.UserResponse owner;
        private FileDto.FileResponse coverImage;
        //private List<StickerDto.StickerResponse> stickers;

        public static DiaryBookResponse from(DiaryBook diaryBook) {
            /*
            List<StickerDto.StickerResponse> stickerResponses = diaryBook.getStickers().stream()
                    .map(StickerDto.StickerResponse::from)
                    .toList();
*/
            return DiaryBookResponse.builder()
                    .id(diaryBook.getId())
                    .title(diaryBook.getTitle())
                    .createAt(diaryBook.getCreatedAt())
                    .lastModified(diaryBook.getLastModifiedAt())
                    .createdBy(UserDto.UserResponse.from(diaryBook.getCreatedBy()))
                    .lastModifiedBy(UserDto.UserResponse.from(diaryBook.getLastModifiedBy()))
                    .owner(UserDto.UserResponse.from(diaryBook.getOwner()))
                    .coverImage(FileDto.FileResponse.from(diaryBook.getCoverImageFile()))
                    //.stickers(stickerResponses)
                    .build();
        }
    }
}
