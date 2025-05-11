package ac.mju.memoria.backend.domain.diary.service;

import ac.mju.memoria.backend.domain.ai.llm.model.AICommentResponse;
import ac.mju.memoria.backend.domain.ai.llm.service.CommentGenerator;
import ac.mju.memoria.backend.domain.diary.dto.AICommentDto;
import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.event.AiCommentNeededEvent;
import ac.mju.memoria.backend.domain.diary.repository.AICommentRepository;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.repository.AICharacterQueryRepository;
import ac.mju.memoria.backend.domain.notification.event.NewAICommentEvent;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AICommentService {
    private final AICommentRepository aiCommentRepository;
    private final DiaryRepository diaryRepository;
    private final CommentGenerator commentGenerator;
    private final AICharacterQueryRepository aicharacterQueryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public List<AICommentDto.AICommentResponse> getAICommentsByDiaryId(Long diaryId) {
        return aiCommentRepository.findByDiaryId(diaryId).stream()
                .map(AICommentDto.AICommentResponse::from)
                .toList();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void generateCommentAsync(AiCommentNeededEvent event) {
        try {
            Diary found = diaryRepository.findById(event.getDiaryId())
                    .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

            List<AICharacter> characters = aicharacterQueryRepository.findByDiaryBookId(found.getDiaryBook().getId());
            if (characters.isEmpty()) {
                log.info("No AI characters found for diary ID: {}", event.getDiaryId());
                return;
            }

            AICharacter character;

            if(event.getDesiredCharacterId() != null) {
                Optional<AICharacter> optionalCharacter = characters.stream()
                        .filter(c -> c.getId().equals(event.getDesiredCharacterId()))
                        .findFirst();

                if (optionalCharacter.isPresent()) {
                    character = optionalCharacter.get();
                } else {
                    log.info("Desired character ID {} not found, picking random character.", event.getDesiredCharacterId());
                    character = characters.get((int) (Math.random() * characters.size()));
                }
            } else {
                character = characters.get((int) (Math.random() * characters.size()));
            }

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
            AIComment saved = aiCommentRepository.save(toSave);

            applicationEventPublisher.publishEvent(
                    new NewAICommentEvent(saved.getId())
            );

        }catch (Exception e) {
            log.error("Error occurred while generating comment: {}", e.getMessage(),e);
            //ignore
        }
    }
}
