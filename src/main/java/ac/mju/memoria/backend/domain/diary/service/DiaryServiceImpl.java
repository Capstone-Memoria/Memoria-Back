package ac.mju.memoria.backend.domain.diary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diary.dto.DiaryRequestDto;
import ac.mju.memoria.backend.domain.diary.dto.DiaryResponseDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

  private final DiaryRepository diaryRepository;
  private final DiaryBookRepository diaryBookRepository;

  @Override
  @Transactional
  public DiaryResponseDto.DiaryResponse createDiary(DiaryRequestDto requestDto, UserDetails userDetails) {
    DiaryBook diaryBook = diaryBookRepository.findById(requestDto.getDiaryBookId())
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    // 사용자가 일기장의 멤버인지 확인
    if (!diaryBook.isMember(userDetails.getUser()) && !diaryBook.getOwner().getEmail().equals(userDetails.getKey())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Diary diary = requestDto.toEntity(diaryBook);
    Diary savedDiary = diaryRepository.save(diary);

    return DiaryResponseDto.DiaryResponse.fromEntity(savedDiary);
  }

  @Override
  @Transactional(readOnly = true)
  public DiaryResponseDto.DiaryResponse getDiary(Long diaryId, Long diaryBookId, UserDetails userDetails) {
    DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    // 사용자가 일기장의 멤버인지 확인
    if (!diaryBook.isMember(userDetails.getUser()) && !diaryBook.getOwner().getEmail().equals(userDetails.getKey())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
        .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

    return DiaryResponseDto.DiaryResponse.fromEntity(diary);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DiaryResponseDto.DiaryResponse> getDiariesByDiaryBook(Long diaryBookId, UserDetails userDetails) {
    DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    // 사용자가 일기장의 멤버인지 확인
    if (!diaryBook.isMember(userDetails.getUser()) && !diaryBook.getOwner().getEmail().equals(userDetails.getKey())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    List<Diary> diaries = diaryRepository.findByDiaryBookOrderByCreatedAtDesc(diaryBook);

    return diaries.stream()
        .map(DiaryResponseDto.DiaryResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public DiaryResponseDto.DiaryResponse updateDiary(Long diaryId, DiaryRequestDto requestDto, UserDetails userDetails) {
    DiaryBook diaryBook = diaryBookRepository.findById(requestDto.getDiaryBookId())
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    // 사용자가 일기장의 멤버인지 확인
    if (!diaryBook.isMember(userDetails.getUser()) && !diaryBook.getOwner().getEmail().equals(userDetails.getKey())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
        .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

    // 작성자만 수정할 수 있음
    if (diary.getCreatedBy() != null && !diary.getCreatedBy().getEmail().equals(userDetails.getKey())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    diary.setTitle(requestDto.getTitle());
    diary.setContent(requestDto.getContent());

    return DiaryResponseDto.DiaryResponse.fromEntity(diary);
  }

  @Override
  @Transactional
  public void deleteDiary(Long diaryId, Long diaryBookId, UserDetails userDetails) {
    DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
        .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

    // 관리자나 소유자만 삭제할 수 있음
    if (!diaryBook.isAdmin(userDetails.getUser())) {
      throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
        .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

    diaryRepository.delete(diary);
  }
}