package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.CustomAICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.entity.CustomAICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.CustomAICharacterRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomAICharacterService {
    private final CustomAICharacterRepository customAICharacterRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;

    @Transactional
    public CustomAICharacterDto.AICharacterResponse createCustomAICharacter(CustomAICharacterDto.CreateRequest request, Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        CustomAICharacter character = request.toEntity();
        diaryBook.addCharacter(character);
        CustomAICharacter saved = customAICharacterRepository.save(character);

        return CustomAICharacterDto.AICharacterResponse.from(saved);
    }

    @Transactional
    public CustomAICharacterDto.AICharacterResponse updateCustomAICharacter(CustomAICharacterDto.UpdateRequest request, Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        CustomAICharacter character = customAICharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        request.applyTo(character);

        return CustomAICharacterDto.AICharacterResponse.from(character);
    }

    @Transactional
    public CustomAICharacterDto.AICharacterResponse getCustomAICharacters(Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        CustomAICharacter findCustom = customAICharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        return CustomAICharacterDto.AICharacterResponse.from(findCustom);
    }

    @Transactional
    public void deleteCustomAICharacter(Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        CustomAICharacter character = customAICharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));

        customAICharacterRepository.delete(character);
    }
}
