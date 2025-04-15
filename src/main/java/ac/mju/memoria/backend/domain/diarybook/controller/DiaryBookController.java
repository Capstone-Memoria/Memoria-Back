package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookService;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.dto.StickerDto;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import ac.mju.memoria.backend.system.security.model.UserDetails;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book")
public class DiaryBookController {
    private final DiaryBookService diaryBookService;

    @PostMapping
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> createDiaryBook(
            @Valid @ModelAttribute DiaryBookDto.DiaryBookCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.createDiaryBook(request, userDetails);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> findDiaryBook(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse diaryBook = diaryBookService.findDiaryBook(id, userDetails);
        return ResponseEntity.ok(diaryBook);
    }

    @GetMapping
    public ResponseEntity<Page<DiaryBookDto.DiaryBookResponse>> getMyDiaryBooks(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DiaryBookDto.DiaryBookResponse> diaryBooks = diaryBookService.getMyDiaryBooks(userDetails, pageable);

        return ResponseEntity.ok(diaryBooks);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DiaryBookDto.DiaryBookResponse> updateDiaryBook(
            @PathVariable Long id, @ModelAttribute DiaryBookDto.DiaryBookUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        DiaryBookDto.DiaryBookResponse updatedDiaryBook = diaryBookService.updateDiaryBook(request, id, userDetails);
        return ResponseEntity.ok(updatedDiaryBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryBook(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {

        diaryBookService.deleteDiaryBook(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
