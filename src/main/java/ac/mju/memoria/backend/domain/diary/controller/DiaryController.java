package ac.mju.memoria.backend.domain.diary.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.service.DiaryService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diary-book/{diaryBookId}/diary")
@RequiredArgsConstructor
@Tag(name = "Diary", description = "다이어리 API")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 생성", description = "새로운 다이어리를 생성합니다. 이미지 파일 첨부가 가능합니다.")
    @ApiResponse(responseCode = "201", description = "다이어리 생성 성공")
    public ResponseEntity<DiaryDto.DiaryResponse> createDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @ModelAttribute @Valid DiaryDto.DiaryRequest requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryService.createDiary(diaryBookId, requestDto, userDetails));
    }

    @GetMapping("/{diaryId}")
    @Operation(summary = "다이어리 상세 조회", description = "특정 다이어리의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 조회 성공")
    public ResponseEntity<DiaryDto.DiaryResponse> getDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(diaryService.getDiary(diaryBookId, diaryId, userDetails));
    }

    @GetMapping
    @Operation(summary = "다이어리 북 내 다이어리 목록 조회", description = "특정 다이어리 북에 속한 다이어리 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 목록 조회 성공")
    public ResponseEntity<Page<DiaryDto.DiaryResponse>> getDiariesByDiaryBook(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DiaryDto.DiaryResponse> diaryPage = diaryService.getDiariesByDiaryBook(diaryBookId, userDetails, pageable);
        return ResponseEntity.ok(diaryPage);
    }

    @PatchMapping(value = "/{diaryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 수정", description = "특정 다이어리의 내용을 수정합니다. 이미지 파일 첨부/삭제가 가능합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 수정 성공")
    public ResponseEntity<DiaryDto.DiaryResponse> updateDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @ModelAttribute @Valid DiaryDto.DiaryUpdateRequest requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(diaryService.updateDiary(diaryBookId, diaryId, requestDto, userDetails));
    }

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "다이어리 삭제", description = "특정 다이어리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "다이어리 삭제 성공")
    public ResponseEntity<Void> deleteDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        diaryService.deleteDiary(diaryBookId, diaryId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-date")
    @Operation(summary = "날짜 범위로 다이어리 목록 조회", description = "특정 다이어리 북에서 지정된 날짜 범위(시작일 포함, 종료일 포함) 내에 작성된 다이어리 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 목록 조회 성공")
    public ResponseEntity<List<DiaryDto.DiaryResponse>> getDiariesByDateRange(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "조회 시작일 (YYYY-MM-DD)") @RequestParam LocalDate startDate,
            @Parameter(description = "조회 종료일 (YYYY-MM-DD)") @RequestParam LocalDate endDate,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<DiaryDto.DiaryResponse> diaries = diaryService.getDiariesByDateRange(diaryBookId, startDate, endDate,
                userDetails);
        return ResponseEntity.ok(diaries);
    }
}