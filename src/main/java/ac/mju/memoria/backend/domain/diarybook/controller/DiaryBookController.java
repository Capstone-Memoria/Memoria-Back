package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book")
@Tag(name = "DiaryBook", description = "다이어리 북 API")
public class DiaryBookController {
    private final DiaryBookService diaryBookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 북 생성", description = "새로운 다이어리 북을 생성합니다. 커버 이미지 첨부가 가능합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 북 생성 성공")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> createDiaryBook(
            @Valid @ModelAttribute DiaryBookDto.DiaryBookCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.createDiaryBook(request, userDetails);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping("/{id}")
    @Operation(summary = "다이어리 북 상세 조회", description = "특정 다이어리 북의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 북 조회 성공")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> findDiaryBook(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.findDiaryBook(id, userDetails);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping
    @Operation(summary = "내 다이어리 북 목록 조회", description = "현재 로그인된 사용자가 참여하고 있는 다이어리 북 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 북 목록 조회 성공")
    public ResponseEntity<Page<DiaryBookDto.DiaryBookResponse>> getMyDiaryBooks(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DiaryBookDto.DiaryBookResponse> diaryBooks = diaryBookService.getMyDiaryBooks(userDetails, pageable);

        return ResponseEntity.ok(diaryBooks);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 북 수정", description = "특정 다이어리 북의 정보를 수정합니다. 커버 이미지 수정/삭제가 가능합니다.")
    @ApiResponse(responseCode = "200", description = "다이어리 북 수정 성공")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> updateDiaryBook(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long id,
            @ModelAttribute DiaryBookDto.DiaryBookUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse updatedDiaryBook = diaryBookService.updateDiaryBook(request, id, userDetails);
        return ResponseEntity.ok(updatedDiaryBook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "다이어리 북 삭제", description = "특정 다이어리 북을 삭제합니다. 해당 다이어리 북의 소유자만 삭제할 수 있습니다.")
    @ApiResponse(responseCode = "204", description = "다이어리 북 삭제 성공")
    public ResponseEntity<Void> deleteDiaryBook(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        diaryBookService.deleteDiaryBook(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
