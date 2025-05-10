package ac.mju.memoria.backend.domain.diarybook.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diarybook.dto.AICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.file.entity.ProfileImage;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AICharacterService {
    private final AICharacterRepository aiCharacterRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final DiaryBookRepository diaryBookRepository;
    private final AICharacterQueryRepository aICharacterQueryRepository;
    private final FileSystemHandler fileSystemHandler;

    @Transactional
    public AICharacterDto.AICharacterResponse createAICharacter(AICharacterDto.CreateRequest request, Long diaryBookId,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        AICharacter character = request.toEntity();
        diaryBook.addCharacter(character);
        AICharacter saved = aiCharacterRepository.save(character);

        ProfileImage profileImage = ProfileImage.from(request.getProfileImage());
        profileImage.setAiCharacter(saved);
        saved.setProfileImage(profileImage);
        fileSystemHandler.saveFile(request.getProfileImage(), profileImage);

        return AICharacterDto.AICharacterResponse.from(saved);
    }

    @Transactional
    public AICharacterDto.AICharacterResponse updateAICharacter(AICharacterDto.UpdateRequest request, Long characterId,
            UserDetails userDetails) {
        AICharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        onlyCustomCharacterCanBeUpdatedOrDeleted(character);

        request.applyTo(character);

        if (Objects.nonNull(request.getProfileImage())) {
            ProfileImage oldProfileImage = character.getProfileImage();
            ProfileImage newProfileImage = ProfileImage.from(request.getProfileImage());

            newProfileImage.setAiCharacter(character);
            character.setProfileImage(newProfileImage);

            fileSystemHandler.deleteFile(oldProfileImage);
            fileSystemHandler.saveFile(request.getProfileImage(), newProfileImage);
        }

        return AICharacterDto.AICharacterResponse.from(character);
    }

    @Transactional
    public AICharacterDto.AICharacterResponse getAICharacter(Long characterId, UserDetails userDetails) {
        AICharacter found = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        return AICharacterDto.AICharacterResponse.from(found);
    }

    @Transactional
    public List<AICharacterDto.AICharacterResponse> findAICharactersByDiaryBook(Long diaryBookId,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        List<AICharacter> characters = aICharacterQueryRepository.findByDiaryBookId(diaryBookId);

        return characters.stream()
                .map(AICharacterDto.AICharacterResponse::from)
                .toList();
    }

    @Transactional
    public void deleteAICharacter(Long characterId, UserDetails userDetails) {
        AICharacter character = aiCharacterRepository.findById(characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        onlyCustomCharacterCanBeUpdatedOrDeleted(character);

        aiCharacterRepository.delete(character);
    }

    private void onlyCustomCharacterCanBeUpdatedOrDeleted(AICharacter character) {
        if (character.getType() != AICharacterType.CUSTOM) {
            throw new RestException(ErrorCode.AI_CHARACTER_NOT_CUSTOM);
        }
    }
}
