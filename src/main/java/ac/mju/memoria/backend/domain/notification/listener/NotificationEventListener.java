package ac.mju.memoria.backend.domain.notification.listener;

import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.repository.AICommentRepository;
import ac.mju.memoria.backend.domain.diary.repository.CommentRepository;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diary.repository.ReactionRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookMemberRepository;
import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import ac.mju.memoria.backend.domain.invitation.repository.InvitationRepository;
import ac.mju.memoria.backend.domain.notification.dto.NotificationDto;
import ac.mju.memoria.backend.domain.notification.entity.Notification;
import ac.mju.memoria.backend.domain.notification.entity.enums.NotificationType;
import ac.mju.memoria.backend.domain.notification.event.*;
import ac.mju.memoria.backend.domain.notification.repository.NotificationRepository;
import ac.mju.memoria.backend.domain.notification.service.SseEmitterService;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final InvitationRepository invitationRepository;
    private final DiaryBookMemberRepository diaryBookMemberRepository;
    private final AICommentRepository aICommentRepository;

    private void saveAndSendNotification(User recipient, NotificationType type, String message, String url) {
        if (recipient == null) return;

        Notification notification = Notification.builder()
                .recipient(recipient)
                .notificationType(type)
                .message(message)
                .url(url)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        sseEmitterService.sendNotification(recipient.getEmail(), NotificationDto.Response.from(saved));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewDiaryEvent(NewDiaryEvent event) {
        Diary diary = diaryRepository.findById(event.getDiaryId())
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));
        DiaryBook diaryBook = diary.getDiaryBook();
        User author = diary.getAuthor();

        String message = String.format("'%s'님이 '%s' 일기장에 새 일기 '%s'를 작성했습니다.",
                author.getNickName(), diaryBook.getTitle(), diary.getTitle());
        String url = String.format("/api/diary-book/%d/diary/%d", diaryBook.getId(), diary.getId());

        List<User> candidates = new ArrayList<>();
        candidates.add(diaryBook.getOwner());
        candidates.addAll(diaryBook.getMembers().stream().map(DiaryBookMember::getUser).toList());
        candidates.removeIf(it -> it.getEmail().equals(author.getEmail()));

        candidates.forEach(member -> saveAndSendNotification(member, NotificationType.NEW_DIARY, message, url));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewReplyEvent(NewReplyEvent event) {
        Comment reply = commentRepository.findById(event.getReplyId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        Comment parentComment = reply.getParent();
        User replier = reply.getUser();
        User originalCommenter = parentComment.getUser();

        String message = String.format("'%s'님이 회원님의 댓글에 답글을 남겼습니다.", replier.getNickName());
        String url = String.format("/api/diary-book/%d/diary/%d/comments",
                reply.getDiary().getDiaryBook().getId(), reply.getDiary().getId());

        saveAndSendNotification(originalCommenter, NotificationType.NEW_REPLY, message, url);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewCommentEvent(NewCommentEvent event) {
        Comment comment = commentRepository.findById(event.getCommentId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        Diary diary = comment.getDiary();
        User commenter = comment.getUser();
        User diaryAuthor = diary.getAuthor();

        String message = String.format("'%s'님이 회원님의 일기 '%s'에 댓글을 남겼습니다.", commenter.getNickName(), diary.getTitle());
        String url = String.format("/diary-book/%d/diary/%d/comments", diary.getDiaryBook().getId(), diary.getId());

        saveAndSendNotification(diaryAuthor, NotificationType.NEW_COMMENT, message, url);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewReactionEvent(NewReactionEvent event) {
        Reaction reaction = reactionRepository.findById(event.getReactionId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        Diary diary = reaction.getId().getDiary();
        User reactor = reaction.getId().getUser();
        User diaryAuthor = diary.getAuthor();

        String message = String.format("'%s'님이 회원님의 일기에 반응을 남겼습니다.", reactor.getNickName());
        String url = String.format("/diary-book/%d/diary/%d", diary.getDiaryBook().getId(), diary.getId());

        saveAndSendNotification(diaryAuthor, NotificationType.NEW_REACTION, message, url);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleInvitationAcceptedEvent(InvitationAcceptedEvent event) {
        Invitation invitation = invitationRepository.findById(event.getInvitationId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        DiaryBookMember newMember = diaryBookMemberRepository.findById(event.getNewMemberId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        User accepter = newMember.getUser();
        User inviter = invitation.getInviteBy();
        DiaryBook diaryBook = newMember.getDiaryBook();

        String message = String.format("'%s'님이 '%s' 일기장 초대를 수락했습니다.",
                accepter.getNickName(), diaryBook.getTitle());
        String url = String.format("/api/diary-book/%d", diaryBook.getId());

        String toMemberMessage = String.format("'%s'님이 '%s' 일기장에 새로 참여했습니다.",
                accepter.getNickName(), diaryBook.getTitle());
        String toMemberUrl = String.format("/diary-book/%d", diaryBook.getId());

        List<User> toSend = new ArrayList<>();
        toSend.add(diaryBook.getOwner());
        toSend.addAll(diaryBook.getMembers().stream().map(DiaryBookMember::getUser).toList());

        saveAndSendNotification(inviter, NotificationType.INVITATION_ACCEPTED, message, url);

        toSend.stream()
                .filter(member -> !member.equals(accepter))
                .forEach(member -> saveAndSendNotification(member, NotificationType.NEW_MEMBER_JOINED, toMemberMessage, toMemberUrl));

        if (invitation instanceof DirectInvitation) {
            invitationRepository.deleteById(invitation.getId());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewInvitationEvent(NewInvitationEvent event) {
        DirectInvitation invitation = (DirectInvitation) invitationRepository.findById(event.getInvitationId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        User inviter = invitation.getInviteBy();
        User invitee = invitation.getInviteTo();
        DiaryBook diaryBook = invitation.getDiaryBook();

        String message = String.format("'%s'님이 회원님을 '%s' 일기장으로 초대했습니다.",
                inviter.getNickName(), diaryBook.getTitle());
        String url = String.format("/api/user/%s", invitee.getEmail());

        saveAndSendNotification(invitee, NotificationType.NEW_INVITATION, message, url);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNewAICommentEvent(NewAICommentEvent event) {
        AIComment found = aICommentRepository.findById(event.getAiCommentId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));
        Diary diary = found.getDiary();

        String message = String.format("'%s'가 회원님의 일기에 편지를 보냈습니다.", found.getCreatedBy().getName());
        String url = String.format("/diary-book/%d/diary/%d", diary.getDiaryBook().getId(), diary.getId());

        saveAndSendNotification(diary.getAuthor(), NotificationType.NEW_AI_COMMENT, message, url);
    }
}
