package ac.mju.memoria.backend.domain.diarybook.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AICharacterDto {
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

        public AICharacter toEntity() {
            return AICharacter.builder()
                    .name(name)
                    .feature(feature)
                    .accent(accent)
                    .type(AICharacterType.CUSTOM)
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

        public void applyTo(AICharacter character) {
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
        private AICharacterType type;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        public static AICharacterResponse from(AICharacter character) {
            return AICharacterResponse.builder()
                    .id(character.getId())
                    .name(character.getName())
                    .profileImage(FileDto.FileResponse.from(character.getProfileImage()))
                    .diaryBook(DiaryBookDto.DiaryBookResponse.from(character.getDiaryBook()))
                    .feature(character.getFeature())
                    .accent(character.getAccent())
                    .type(character.getType())
                    .createdAt(character.getCreatedAt())
                    .lastModifiedAt(character.getLastModifiedAt())
                    .build();
        }
    }
}
