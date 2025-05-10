package ac.mju.memoria.backend.domain.diary.service;

import ac.mju.memoria.backend.domain.ai.llm.model.AICommentResponse;
import ac.mju.memoria.backend.domain.ai.llm.service.CommentGenerator;
import ac.mju.memoria.backend.domain.diary.dto.AICommentDto;
import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.AICommentRepository;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterQueryRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AICommentService {
    private final AICommentRepository aiCommentRepository;
    private final DiaryRepository diaryRepository;
    private final CommentGenerator commentGenerator;
    private final AICharacterQueryRepository aicharacterQueryRepository;

    @Transactional(readOnly = true)
    public List<AICommentDto.AICommentResponse> getAICommentsByDiaryId(Long diaryId) {
        return aiCommentRepository.findByDiaryId(diaryId).stream()
                .map(AICommentDto.AICommentResponse::from)
                .toList();
    }

    @Async
    @Transactional
    public void generateCommentAsync(Long diaryId) {
        try {
            Diary found = diaryRepository.findById(diaryId)
                    .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

            List<AICharacter> characters = aicharacterQueryRepository.findByDiaryBookId(found.getDiaryBook().getId());
            if (characters.isEmpty()) {
                log.info("No AI characters found for diary ID: {}", diaryId);
                return;
            }

            //pick random character
            AICharacter character = characters.get((int) (Math.random() * characters.size()));
            AICommentResponse generated = commentGenerator.generateComment(
                    found.getTitle(),
                    found.getContent(),
                    character.getName(),
                    character.getFeature(),
                    character.getAccent()
            );

            //save comment
            AIComment toSave = AIComment.builder()
                    .diary(found)
                    .createdBy(character)
                    .title(generated.getTitle())
                    .content(generated.getContent())
                    .build();
            aiCommentRepository.save(toSave);

        }catch (Exception e) {
            log.error("Error occurred while generating comment: {}", e.getMessage());
            //ignore
        }
    }
}
