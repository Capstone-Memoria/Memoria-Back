package ac.mju.memoria.backend.domain.diary.service;

import java.util.List;

import ac.mju.memoria.backend.domain.diary.dto.DiaryRequestDto;
import ac.mju.memoria.backend.domain.diary.dto.DiaryResponseDto;
import ac.mju.memoria.backend.system.security.model.UserDetails;

public interface DiaryService {
  DiaryResponseDto.DiaryResponse createDiary(DiaryRequestDto requestDto, UserDetails userDetails);

  DiaryResponseDto.DiaryResponse getDiary(Long diaryId, Long diaryBookId, UserDetails userDetails);

  List<DiaryResponseDto.DiaryResponse> getDiariesByDiaryBook(Long diaryBookId, UserDetails userDetails);

  DiaryResponseDto.DiaryResponse updateDiary(Long diaryId, DiaryRequestDto requestDto, UserDetails userDetails);

  void deleteDiary(Long diaryId, Long diaryBookId, UserDetails userDetails);
}