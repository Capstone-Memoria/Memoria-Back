package ac.mju.memoria.backend.domain.diary.service;

import ac.mju.memoria.backend.domain.diary.dto.ReactionDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diary.repository.ReactionRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public ReactionDto.Response addReaction(Long diaryId, ReactionDto.Request request, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        reactionRepository.findByDiaryAndUser(diary, user).ifPresent(
                e -> {
                    throw new RestException(ErrorCode.REACTION_ALREADY_EXISTS);
                }
        );

        Reaction reaction = Reaction.builder()
                .type(request.getReactionType())
                .diary(diary)
                .user(user)
                .build();

        user.addReaction(reaction);

        Reaction saved = reactionRepository.save(reaction);
        return ReactionDto.Response.from(saved);
    }

    @Transactional
    public ReactionDto.Response updateReaction(Long diaryId, ReactionDto.Request request, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        Reaction reaction = reactionRepository.findByDiaryAndUser(diary, user)
                .orElseThrow(() ->  new RestException(ErrorCode.REACTION_NOT_FOUND));

        reaction.setType(request.getReactionType());

        return ReactionDto.Response.from(reaction);
    }

    @Transactional
    public void deleteReaction(Long diaryId, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        Reaction reaction = reactionRepository.findByDiaryAndUser(diary, user)
                .orElseThrow(() ->  new RestException(ErrorCode.REACTION_NOT_FOUND));

        reactionRepository.delete(reaction);
    }

    @Transactional(readOnly = true)
    public List<ReactionDto.Response> getReactionsForDiary(Long diaryId, UserDetails userDetails) {
        User user = userDetails.getUser();
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.isDiaryBookMember(user);

        List<Reaction> reactions = diary.getReactions();

        return reactions.stream()
                .map(ReactionDto.Response::from)
                .collect(Collectors.toList());
    }

}
