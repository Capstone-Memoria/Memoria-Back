package ac.mju.memoria.backend.domain.diary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diary.dto.CommentDto;
import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.CommentQueryRepository;
import ac.mju.memoria.backend.domain.diary.repository.CommentRepository;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;

    @Transactional
    public CommentDto.CommentResponse createComment(Long diaryBookId, Long diaryId,
            CommentDto.UserCommentRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        Comment toSave = request.toEntity();
        toSave.setDiary(diary);
        toSave.setUser(userDetails.getUser());

        Comment saved = commentRepository.save(toSave);

        return CommentDto.CommentResponse.from(saved);
    }

    @Transactional
    public CommentDto.CommentResponse createReply(Long parentId, CommentDto.UserCommentRequest request,
            UserDetails userDetails) {
        Comment parent = commentQueryRepository.findById(parentId)
                .orElseThrow(() -> new RestException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

        Comment toSave = request.toEntity();
        toSave.setParent(parent);
        toSave.setDiary(parent.getDiary());
        toSave.setUser(userDetails.getUser());

        Comment saved = commentRepository.save(toSave);

        return CommentDto.CommentResponse.from(saved);
    }

    @Transactional
    public CommentDto.CommentResponse updateComment(Long commentId, CommentDto.UserCommentRequest request,
            UserDetails userDetails) {
        Comment toUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestException(ErrorCode.COMMENT_NOT_FOUND));

        if (Objects.nonNull(request.getContent()))
            toUpdate.setContent(request.getContent());

        return CommentDto.CommentResponse.from(toUpdate);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.TreeResponse> getCommentsByDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        return commentQueryRepository.findCommentsWithChildrenByDiary(diary)
                .stream().map(CommentDto.TreeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Long getCommentCountByDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        return commentRepository.countByDiary(diary);
    }

    @Transactional
    public void deleteComment(Long commentId, UserDetails userDetails) {
        Comment toDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestException(ErrorCode.COMMENT_NOT_FOUND));

        if (toDelete.getChildren().isEmpty()) {
            List<Comment> deleteList = new ArrayList<>();

            Comment parent = toDelete.getParent();
            deleteList.add(toDelete);

            while (true) {
                if (Objects.nonNull(parent) && parent.isDeleted()) {
                    deleteList.add(parent);
                    parent = parent.getParent();
                } else
                    break;
            }

            commentRepository.deleteAll(deleteList);
        } else {
            toDelete.setDeleted(true);
        }
    }
}
