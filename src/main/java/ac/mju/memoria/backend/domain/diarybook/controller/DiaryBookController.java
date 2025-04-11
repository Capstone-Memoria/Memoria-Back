package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book")
public class DiaryBookController {
    private final DiaryBookService diaryBookService;

    @PostMapping
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> createDiaryBook(
            @Valid @RequestBody DiaryBookDto.DiaryBookCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getKey();
        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.createDiaryBook(request, userEmail);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> getDiaryBook(
            @PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getKey();
        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.getDiaryBook(id, userEmail);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping
    public ResponseEntity<Page<DiaryBookDto.DiaryBookResponse>> getMyDiaryBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String userEmail = userDetails.getKey();
        Page<DiaryBookDto.DiaryBookResponse> diaryBooks = diaryBookService.getMyDiaryBooks(userEmail, pageable);

        return ResponseEntity.ok(diaryBooks);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> updateDiaryBook(
            @PathVariable Integer id, @RequestBody DiaryBookDto.DiaryBookUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getKey();
        DiaryBookDto.DiaryBookResponse updatedDiaryBook = diaryBookService.updateDiaryBook(request, id, userEmail);
        return ResponseEntity.ok(updatedDiaryBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryBook(
            @PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getKey();
        diaryBookService.deleteDiaryBook(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
