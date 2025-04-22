package ac.mju.memoria.backend.domain.diary.controller;

import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.service.DiaryService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary-book/{diaryBookId}/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryDto.DiaryResponse> createDiary(
            @PathVariable Long diaryBookId,
            @ModelAttribute @Valid DiaryDto.DiaryRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryService.createDiary(diaryBookId, requestDto, userDetails));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryDto.DiaryResponse> getDiary(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(diaryService.getDiary(diaryBookId, diaryId, userDetails));
    }

    @GetMapping
    public ResponseEntity<Page<DiaryDto.DiaryResponse>> getDiariesByDiaryBook(
            @PathVariable Long diaryBookId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DiaryDto.DiaryResponse> diaryPage = diaryService.getDiariesByDiaryBook(diaryBookId, userDetails, pageable);
        return ResponseEntity.ok(diaryPage);
    }

    @PatchMapping("/{diaryId}")
    public ResponseEntity<DiaryDto.DiaryResponse> updateDiary(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @ModelAttribute @Valid DiaryDto.DiaryUpdateRequest requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(diaryService.updateDiary(diaryBookId, diaryId, requestDto, userDetails));
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        diaryService.deleteDiary(diaryBookId, diaryId, userDetails);
        return ResponseEntity.noContent().build();
    }
}