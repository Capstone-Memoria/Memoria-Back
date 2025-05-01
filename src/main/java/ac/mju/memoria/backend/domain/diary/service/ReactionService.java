package ac.mju.memoria.backend.domain.diary.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.diary.repository.ReactionQueryRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diary.dto.ReactionDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.ReactionId;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diary.repository.ReactionRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final DiaryRepository diaryRepository;
    private final ReactionQueryRepository reactionQueryRepository;

    @Transactional
    @Nullable
    public ReactionDto.Response reactToDiary(Long diaryId, ReactionDto.Request request, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        Reaction toModify = reactionRepository.findById(ReactionId.of(diary, userDetails.getUser()))
                .orElseGet(() -> saveAndGetNewReaction(diary, user, request));

        if (Objects.isNull(request.getReactionType())) {
            reactionRepository.delete(toModify);
            return null;
        } else {
            toModify.setType(request.getReactionType());
        }

        return ReactionDto.Response.from(toModify);
    }

    private Reaction saveAndGetNewReaction(Diary diary, User user, ReactionDto.Request request) {
        Reaction reaction = Reaction.builder()
                .id(ReactionId.of(diary, user))
                .type(request.getReactionType())
                .build();
        return reactionRepository.save(reaction);
    }

    @Transactional(readOnly = true)
    public List<ReactionDto.Response> getReactionsForDiary(Long diaryId, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        List<Reaction> reactions = reactionQueryRepository.findByDiaryId(diaryId);

        return reactions.stream()
                .map(ReactionDto.Response::from)
                .collect(Collectors.toList());
    }

}
