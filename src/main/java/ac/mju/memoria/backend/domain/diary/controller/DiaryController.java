package ac.mju.memoria.backend.domain.diary.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diary.dto.DiaryRequestDto;
import ac.mju.memoria.backend.domain.diary.dto.DiaryResponseDto;
import ac.mju.memoria.backend.domain.diary.service.DiaryService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

  private final DiaryService diaryService;

  @PostMapping
  public ResponseEntity<DiaryResponseDto.DiaryResponse> createDiary(
      @RequestBody @Valid DiaryRequestDto requestDto,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(diaryService.createDiary(requestDto, userDetails));
  }

  @GetMapping("/{diaryId}")
  public ResponseEntity<DiaryResponseDto.DiaryResponse> getDiary(
      @PathVariable Long diaryId,
      @RequestParam Long diaryBookId,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(diaryService.getDiary(diaryId, diaryBookId, userDetails));
  }

  @GetMapping("/diarybook/{diaryBookId}")
  public ResponseEntity<List<DiaryResponseDto.DiaryResponse>> getDiariesByDiaryBook(
      @PathVariable Long diaryBookId,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(diaryService.getDiariesByDiaryBook(diaryBookId, userDetails));
  }

  @PutMapping("/{diaryId}")
  public ResponseEntity<DiaryResponseDto.DiaryResponse> updateDiary(
      @PathVariable Long diaryId,
      @RequestBody @Valid DiaryRequestDto requestDto,
      @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.ok(diaryService.updateDiary(diaryId, requestDto, userDetails));
  }

  @DeleteMapping("/{diaryId}")
  public ResponseEntity<Void> deleteDiary(
      @PathVariable Long diaryId,
      @RequestParam Long diaryBookId,
      @AuthenticationPrincipal UserDetails userDetails) {
    diaryService.deleteDiary(diaryId, diaryBookId, userDetails);
    return ResponseEntity.noContent().build();
  }
}