package ac.mju.memoria.backend.domain.diarybook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diarybook.dto.AICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AICharacterService {
    private final AICharacterRepository aiCharacterRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;

    @Transactional
    public AICharacterDto.AICharacterResponse createAICharacter(AICharacterDto.CreateRequest request, Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        AICharacter character = request.toEntity();
        diaryBook.addCharacter(character);
        AICharacter saved = aiCharacterRepository.save(character);

        return AICharacterDto.AICharacterResponse.from(saved);
    }

    @Transactional
    public AICharacterDto.AICharacterResponse updateAICharacter(AICharacterDto.UpdateRequest request, Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        AICharacter character = aiCharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));
        
        validateCustomType(character);
        
        request.applyTo(character);

        return AICharacterDto.AICharacterResponse.from(character);
    }

    @Transactional
    public AICharacterDto.AICharacterResponse getAICharacter(Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        AICharacter findCharacter = aiCharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));
        
        validateCustomType(findCharacter);

        return AICharacterDto.AICharacterResponse.from(findCharacter);
    }

    @Transactional
    public List<AICharacterDto.AICharacterResponse> getAllAICharacters(Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        List<AICharacter> characters = aiCharacterRepository.findAllByDiaryBook(diaryBook);

        return characters.stream()
                .map(AICharacterDto.AICharacterResponse::from)
                .toList();
    }

    @Transactional
    public void deleteAICharacter(Long diaryBookId, Long characterId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        AICharacter character = aiCharacterRepository.findByDiaryBookAndId(diaryBook, characterId)
                .orElseThrow(() -> new RestException(ErrorCode.AI_CHARACTER_NOT_FOUND));
        
        validateCustomType(character);

        aiCharacterRepository.delete(character);
    }
    
    private void validateCustomType(AICharacter character) {
        if (character.getType() != AICharacterType.CUSTOM) {
            throw new RestException(ErrorCode.AI_CHARACTER_NOT_CUSTOM);
        }
    }
}
