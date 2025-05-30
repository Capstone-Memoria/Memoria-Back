package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.AbstractSticker;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiaryBookDto {

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Schema(description = "다이어리 북 생성 요청 DTO")
    public static class DiaryBookCreateRequest {
        @NotBlank(message = "제목을 입력해 주세요")
        @Schema(description = "다이어리 북 제목", example = "우리의 추억 앨범")
        private String title;
        @Schema(description = "다이어리 북 커버 이미지 파일")
        private MultipartFile coverImage;
        @Schema(description = "다이어리 북 책등 색상", example = "#FF5733")
        private String spineColor;

        public DiaryBook toEntity(User user) {
            return DiaryBook.builder()
                    .title(title)
                    .owner(user)
                    .spineColor(spineColor)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Schema(description = "다이어리 북 수정 요청 DTO")
    public static class DiaryBookUpdateRequest {
        @Schema(description = "수정할 다이어리 북 제목", example = "새로운 추억 앨범")
        private String title;
        @Schema(description = "수정할 다이어리 북 커버 이미지 파일")
        private MultipartFile coverImage;
        @Schema(description = "수정할 다이어리 북 책등 색상", example = "#FF5733")
        private String spineColor;

        public void applyTo(DiaryBook diaryBook) {
            if (Objects.nonNull(title)) {
                diaryBook.setTitle(title);
            }
            if (Objects.nonNull(spineColor)) {
                diaryBook.setSpineColor(spineColor);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Schema(description = "다이어리 북 응답 DTO")
    public static class DiaryBookResponse {
        @Schema(description = "다이어리 북 ID")
        private Long id;
        @Schema(description = "다이어리 북 제목")
        private String title;
        @Schema(description = "다이어리 북 고정 여부")
        private Boolean isPinned;
        @Schema(description = "다이어리 북 책등 색상")
        private String spineColor;
        @Schema(description = "다이어리 북 생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "다이어리 북 마지막 수정 시간")
        private LocalDateTime lastModifiedAt;
        @Schema(description = "다이어리 북 생성자 정보")
        private UserDto.UserResponse createdBy;
        @Schema(description = "다이어리 북 마지막 수정자 정보")
        private UserDto.UserResponse lastModifiedBy;
        @Schema(description = "다이어리 북 소유자 정보")
        private UserDto.UserResponse owner;
        @Schema(description = "다이어리 북 커버 이미지 정보")
        private FileDto.FileResponse coverImage;
        @Schema(description = "다이어리 북에 부착된 스티커 목록")
        private List<StickerDto.AbstractResponse> stickers;
        @Schema(description = "다이어리 북 참여 멤버 수 (소유자 포함)")
        private Integer memberCount;

        public static DiaryBookResponse from(DiaryBook diaryBook) {
            return from(diaryBook, false);
        }

        public static DiaryBookResponse from(DiaryBook diaryBook, boolean isPinned) {
            return DiaryBookResponse.builder()
                    .id(diaryBook.getId())
                    .title(diaryBook.getTitle())
                    .isPinned(isPinned)
                    .spineColor(diaryBook.getSpineColor())
                    .createdAt(diaryBook.getCreatedAt())
                    .lastModifiedAt(diaryBook.getLastModifiedAt())
                    .createdBy(UserDto.UserResponse.from(diaryBook.getCreatedBy()))
                    .lastModifiedBy(UserDto.UserResponse.from(diaryBook.getLastModifiedBy()))
                    .owner(UserDto.UserResponse.from(diaryBook.getOwner()))
                    .coverImage(FileDto.FileResponse.from(diaryBook.getCoverImageFile()))
                    .stickers(
                            diaryBook.getAbstractStickers().stream()
                                    .map(StickerDto.AbstractResponse::from).toList())
                    .memberCount(diaryBook.getMembers() == null ? 1 : diaryBook.getMembers().size() + 1) // 소유자 포함
                    .build();
        }
    }
}
