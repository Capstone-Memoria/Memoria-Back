package ac.mju.memoria.backend.domain.diarybook.dto;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        // @NotNull(message = "커버이미지를 업로드해 주세요") TODO: 추후 활성화
        @Schema(description = "다이어리 북 커버 이미지 파일")
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
    @Schema(description = "다이어리 북 수정 요청 DTO")
    public static class DiaryBookUpdateRequest {
        @Schema(description = "수정할 다이어리 북 제목", example = "새로운 추억 앨범")
        private String title;
        @Schema(description = "다이어리 북 고정 여부", example = "true")
        private Boolean isPinned;
        @Schema(description = "수정할 다이어리 북 커버 이미지 파일")
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
    @Schema(description = "다이어리 북 응답 DTO")
    public static class DiaryBookResponse {
        @Schema(description = "다이어리 북 ID")
        private Long id;
        @Schema(description = "다이어리 북 제목")
        private String title;
        @Schema(description = "다이어리 북 고정 여부")
        private Boolean isPinned;
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
        private List<StickerDto.StickerResponse> stickers;
        @Schema(description = "다이어리 북 참여 멤버 수 (소유자 포함)")
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
                    .memberCount(diaryBook.getMembers() == null ? 1 : diaryBook.getMembers().size() + 1) // 소유자 포함
                    .build();
        }
    }
}
