package ac.mju.memoria.backend.domain.comment.service;

import ac.mju.memoria.backend.domain.comment.dto.CommentDto;
import ac.mju.memoria.backend.domain.comment.entity.Comment;
import ac.mju.memoria.backend.domain.comment.entity.UserComment;
import ac.mju.memoria.backend.domain.comment.repository.CommentQueryRepository;
import ac.mju.memoria.backend.domain.comment.repository.CommentRepository;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;
    private final CommentQueryRepository userCommentQueryRepository;
    private final CommentQueryRepository commentQueryRepository;

    @Transactional
    public CommentDto.CommentResponse createComment(Long diaryBookId, Long diaryId, CommentDto.UserCommentRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        Comment toSave = request.toEntity();
        toSave.setDiary(diary);

        Comment saved = commentRepository.save(toSave);

        return CommentDto.CommentResponse.from(saved);
    }

    @Transactional
    public CommentDto.CommentResponse createReply(Long parentId, CommentDto.UserCommentRequest request, UserDetails userDetails) {
        Comment parent = userCommentQueryRepository.findById(parentId)
                .orElseThrow(() -> new RestException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

        Comment toSave = request.toEntity();
        toSave.setParent(parent);
        toSave.setDiary(parent.getDiary());

        Comment saved = commentRepository.save(toSave);

        return CommentDto.CommentResponse.from(saved);
    }

    @Transactional
    public CommentDto.CommentResponse updateComment(Long commentId, CommentDto.UserCommentRequest request, UserDetails userDetails) {
        Comment toUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestException(ErrorCode.COMMENT_NOT_FOUND));

        if (Objects.nonNull(request.getContent())) toUpdate.setContent(request.getContent());

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
                } else break;
            }

            commentRepository.deleteAll(deleteList);
        } else {
            toDelete.setDeleted(true);
        }
    }
}
