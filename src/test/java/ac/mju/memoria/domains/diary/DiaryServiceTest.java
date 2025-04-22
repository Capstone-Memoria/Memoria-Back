package ac.mju.memoria.domains.diary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.service.DiaryService;
import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookService;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.auditor.AuditorAwareImpl;
import ac.mju.memoria.backend.system.security.model.UserDetails;

@SpringBootTest
@ActiveProfiles("local")
@WithMockUser(username = "test@example.com")
public class DiaryServiceTest {
    @Autowired
    DiaryService diaryService;
    @Autowired
    DiaryBookService diaryBookService;
    @Autowired
    DiaryBookRepository diaryBookRepository;
    @Autowired
    UserRepository userRepository;

    @MockitoBean
    AuditorAwareImpl auditorAware;

    private UserDetails userDetails;
    private DiaryBook diaryBook;
    private DiaryDto.DiaryRequest diaryRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .build();
        userRepository.saveAndFlush(testUser);
        userDetails = new UserDetails(testUser);

        // AuditorAware 모킹 설정
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(testUser));

        // 테스트용 다이어리북 생성
        DiaryBookDto.DiaryBookCreateRequest diaryBookRequest = DiaryBookDto.DiaryBookCreateRequest.builder()
                .title("Test DiaryBook")
                .build();
        DiaryBookDto.DiaryBookResponse diaryBookResponse = diaryBookService.createDiaryBook(diaryBookRequest,
                userDetails);
        diaryBook = diaryBookRepository.findById(diaryBookResponse.getId())
                .orElseThrow(() -> new RuntimeException("DiaryBook not found"));

        // 테스트용 다이어리 요청 생성
        diaryRequest = DiaryDto.DiaryRequest.builder()
                .title("Test Diary")
                .content("Test Content")
                .images(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("다이어리 생성 테스트")
    void createDiary() {
        // when
        DiaryDto.DiaryResponse response = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // then
        assertThat(response.getTitle()).isEqualTo(diaryRequest.getTitle());
        assertThat(response.getContent()).isEqualTo(diaryRequest.getContent());
        assertThat(response.getDiaryBookId()).isEqualTo(diaryBook.getId());
        assertThat(response.getCreatedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(response.getLastModifiedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
    }

    @Test
    @DisplayName("다이어리 조회 테스트")
    void getDiary() {
        // given
        DiaryDto.DiaryResponse createdDiary = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // when
        DiaryDto.DiaryResponse foundDiary = diaryService.getDiary(diaryBook.getId(), createdDiary.getId(), userDetails);

        // then
        assertThat(foundDiary.getId()).isEqualTo(createdDiary.getId());
        assertThat(foundDiary.getTitle()).isEqualTo(createdDiary.getTitle());
        assertThat(foundDiary.getContent()).isEqualTo(createdDiary.getContent());
        assertThat(foundDiary.getCreatedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(foundDiary.getLastModifiedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(foundDiary.getCreatedAt()).isNotNull();
        assertThat(foundDiary.getLastModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("다이어리 목록 조회 테스트")
    void getDiariesByDiaryBook() {
        // given
        diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<DiaryDto.DiaryResponse> diaries = diaryService.getDiariesByDiaryBook(diaryBook.getId(), userDetails,
                pageable);

        // then
        assertThat(diaries.getContent()).hasSize(1);
        DiaryDto.DiaryResponse diary = diaries.getContent().get(0);
        assertThat(diary.getTitle()).isEqualTo(diaryRequest.getTitle());
        assertThat(diary.getContent()).isEqualTo(diaryRequest.getContent());
        assertThat(diary.getCreatedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(diary.getLastModifiedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(diary.getCreatedAt()).isNotNull();
        assertThat(diary.getLastModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("다이어리 수정 테스트")
    void updateDiary() {
        // given
        // 이미지를 포함한 다이어리 생성
        MockMultipartFile image1 = new MockMultipartFile(
                "image1",
                "test1.jpg",
                "image/jpeg",
                "test image content 1".getBytes());
        List<MultipartFile> initialImages = List.of(image1);
        diaryRequest.setImages(initialImages);
        DiaryDto.DiaryResponse createdDiary = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // 새로운 이미지 준비
        MockMultipartFile image2 = new MockMultipartFile(
                "image2",
                "test2.jpg",
                "image/jpeg",
                "test image content 2".getBytes());
        List<MultipartFile> newImages = List.of(image2);

        // 삭제할 이미지 ID 준비 (첫 번째 이미지 삭제)
        List<String> toDeleteImageIds = createdDiary.getImages().stream()
                .map(image -> image.getId())
                .toList();

        DiaryDto.DiaryUpdateRequest updateRequest = DiaryDto.DiaryUpdateRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .toAddImages(newImages)
                .toDeleteImageIds(toDeleteImageIds)
                .build();

        // when
        DiaryDto.DiaryResponse updatedDiary = diaryService.updateDiary(
                diaryBook.getId(),
                createdDiary.getId(),
                updateRequest,
                userDetails);

        // then
        assertThat(updatedDiary.getId()).isEqualTo(createdDiary.getId());
        assertThat(updatedDiary.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(updatedDiary.getContent()).isEqualTo(updateRequest.getContent());
        assertThat(updatedDiary.getCreatedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());
        assertThat(updatedDiary.getLastModifiedBy().getEmail()).isEqualTo(userDetails.getUser().getEmail());

        // 이미지 확인: 이전 이미지는 삭제되고 새 이미지만 존재해야 함
        assertThat(updatedDiary.getImages()).hasSize(1);
        assertThat(updatedDiary.getImages().get(0).getFileName()).isEqualTo("test2.jpg");
        assertThat(updatedDiary.getImages().get(0).getSize()).isEqualTo("test image content 2".getBytes().length);
    }

    @Test
    @DisplayName("다이어리 이미지 추가만 테스트")
    void updateDiaryAddImagesOnly() {
        // given
        // 이미지 없는 다이어리 생성
        DiaryDto.DiaryResponse createdDiary = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // 새로운 이미지 준비
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        List<MultipartFile> newImages = List.of(image);

        DiaryDto.DiaryUpdateRequest updateRequest = DiaryDto.DiaryUpdateRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .toAddImages(newImages)
                .build();

        // when
        DiaryDto.DiaryResponse updatedDiary = diaryService.updateDiary(
                diaryBook.getId(),
                createdDiary.getId(),
                updateRequest,
                userDetails);

        // then
        assertThat(updatedDiary.getImages()).hasSize(1);
        assertThat(updatedDiary.getImages().get(0).getFileName()).isEqualTo("test.jpg");
        assertThat(updatedDiary.getImages().get(0).getSize()).isEqualTo("test image content".getBytes().length);
    }

    @Test
    @DisplayName("다이어리 삭제 테스트")
    void deleteDiary() {
        // given
        // 이미지를 포함한 다이어리 생성
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        List<MultipartFile> images = List.of(image);
        diaryRequest.setImages(images);
        DiaryDto.DiaryResponse createdDiary = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);
        Long diaryId = createdDiary.getId();
        Long diaryBookId = diaryBook.getId();

        // when - 다이어리 삭제
        diaryService.deleteDiary(diaryBookId, diaryId, userDetails);

        // then - 다이어리 조회 시 예외가 발생해야 함
        assertThat(
                assertThrows(
                        ac.mju.memoria.backend.system.exception.model.RestException.class,
                        () -> diaryService.getDiary(diaryBookId, diaryId, userDetails))
                        .getErrorCode())
                .isEqualTo(ac.mju.memoria.backend.system.exception.model.ErrorCode.DIARY_NOT_FOUND);
    }

    @Test
    @DisplayName("다이어리와 이미지 삭제 테스트")
    void deleteAndVerifyDiary() {
        // given
        // 이미지를 포함한 다이어리 생성
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        List<MultipartFile> images = List.of(image);
        diaryRequest.setImages(images);
        DiaryDto.DiaryResponse createdDiary = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // 이미지가 생성되었는지 확인
        assertThat(createdDiary.getImages()).hasSize(1);

        // when - 다이어리 삭제
        diaryService.deleteDiary(diaryBook.getId(), createdDiary.getId(), userDetails);

        // then - 다이어리 조회 시 예외가 발생해야 함
        assertThat(
                assertThrows(
                        ac.mju.memoria.backend.system.exception.model.RestException.class,
                        () -> diaryService.getDiary(diaryBook.getId(), createdDiary.getId(), userDetails))
                        .getErrorCode())
                .isEqualTo(ac.mju.memoria.backend.system.exception.model.ErrorCode.DIARY_NOT_FOUND);
    }

    @Test
    @DisplayName("이미지가 포함된 다이어리 생성 테스트")
    void createDiaryWithImages() {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        List<MultipartFile> images = List.of(image);
        diaryRequest.setImages(images);

        // when
        DiaryDto.DiaryResponse response = diaryService.createDiary(diaryBook.getId(), diaryRequest, userDetails);

        // then
        assertThat(response.getImages()).hasSize(1);
        assertThat(response.getImages().get(0).getFileName()).isEqualTo("test.jpg");
        assertThat(response.getImages().get(0).getSize()).isEqualTo("test image content".getBytes().length);
    }
}
