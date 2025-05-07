package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.CustomAICharacter;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

public class CustomAICharacterDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "AI캐릭터의 이름을 입력하세요")
        private String name;

        @NotBlank(message = "AI캐릭터의 특징을 입력하세요")
        private String feature;

        @NotBlank(message = "AI캐릭터의 말투를 입력하세요")
        private String accent;

        public CustomAICharacter toEntity() {
            return CustomAICharacter.builder()
                    .name(name)
                    .feature(feature)
                    .accent(accent)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class UpdateRequest {
        private String name;
        private String feature;
        private String accent;

        public void applyTo(CustomAICharacter character) {
            if (Objects.nonNull(name)) {
                character.setName(name);
            }
            if (Objects.nonNull(feature)) {
                character.setFeature(feature);
            }
            if (Objects.nonNull(accent)) {
                character.setAccent(accent);
            }
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class AICharacterResponse {
        private Long id;
        private String name;
        private FileDto.FileResponse profileImage;
        private DiaryBookDto.DiaryBookResponse diaryBook;
        private String feature;
        private String accent;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        public static AICharacterResponse from(CustomAICharacter custom) {
            return AICharacterResponse.builder()
                    .id(custom.getId())
                    .name(custom.getName())
                    .profileImage(FileDto.FileResponse.from(custom.getProfileImage()))
                    .diaryBook(DiaryBookDto.DiaryBookResponse.from(custom.getDiaryBook()))
                    .feature(custom.getFeature())
                    .accent(custom.getAccent())
                    .createdAt(custom.getCreatedAt())
                    .lastModifiedAt(custom.getLastModifiedAt())
                    .build();
        }
    }
}
