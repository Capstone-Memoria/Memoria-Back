package ac.mju.memoria.backend.domain.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ac.mju.memoria.backend.domain.ai.model.MusicCreationQueueItem;
import ac.mju.memoria.backend.domain.ai.model.MusicCreationResult;
import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.file.entity.MusicFile;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;

@ExtendWith(MockitoExtension.class)
public class MusicCreateServiceTest {

  @Mock
  private DiaryRepository diaryRepository;

  @Mock
  private AttachedFileRepository attachedFileRepository;

  @Mock
  private FileSystemHandler fileSystemHandler;

  @Mock
  private MusicServerNode mockNode;

  @InjectMocks
  private MusicCreateService musicCreateService;

  private List<MusicServerNode> musicServerNodes;

  @BeforeEach
  void setUp() {
    musicServerNodes = new ArrayList<>();
    musicServerNodes.add(mockNode);
    ReflectionTestUtils.setField(musicCreateService, "musicServerNodes", musicServerNodes);
    ReflectionTestUtils.setField(musicCreateService, "fileSavePath", "/tmp");
  }

  @Test
  void addToQueue_ShouldAddItemToQueue() {
    // Given
    Long diaryId = 1L;
    MusicCreationQueueItem item = new MusicCreationQueueItem(diaryId, "pop", "테스트 가사");

    // Mocking node's isBusy to return false (available)
    AtomicBoolean isBusy = new AtomicBoolean(false);
    when(mockNode.getIsBusy()).thenReturn(isBusy);
    when(mockNode.createMusic(any(MusicCreationQueueItem.class)))
        .thenReturn(new MusicCreationResult(diaryId, new ByteArrayInputStream("test music data".getBytes())));

    // When
    musicCreateService.addToQueue(item);

    // Then - Verify the item was processed (since our mock node is available)
    verify(mockNode, timeout(1000)).createMusic(any(MusicCreationQueueItem.class));
  }

  @Test
  void isAnyNodeBusy_ShouldReturnTrueWhenNodeIsBusy() {
    // Given
    AtomicBoolean isBusy = new AtomicBoolean(true);
    when(mockNode.getIsBusy()).thenReturn(isBusy);

    // When
    boolean result = musicCreateService.isAnyNodeBusy();

    // Then
    assertTrue(result);
  }

  @Test
  void isAnyNodeBusy_ShouldReturnFalseWhenNoNodeIsBusy() {
    // Given
    AtomicBoolean isBusy = new AtomicBoolean(false);
    when(mockNode.getIsBusy()).thenReturn(isBusy);

    // When
    boolean result = musicCreateService.isAnyNodeBusy();

    // Then
    assertFalse(result);
  }

  @Test
  void handleMusicCreationResult_ShouldSaveMusicFileAndUpdateDiary() throws Exception {
    // Given
    Long diaryId = 1L;
    byte[] musicData = "test music data".getBytes();
    InputStream is = new ByteArrayInputStream(musicData);

    MusicCreationResult result = new MusicCreationResult(diaryId, is);

    Diary mockDiary = mock(Diary.class);
    when(mockDiary.getId()).thenReturn(diaryId);

    when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(mockDiary));
    when(fileSystemHandler.saveStream(any(InputStream.class), any(MusicFile.class))).thenReturn(1024L);
    when(attachedFileRepository.save(any(MusicFile.class))).thenAnswer(i -> i.getArgument(0));

    // When - indirectly calling handleMusicCreationResult
    AtomicBoolean isBusy = new AtomicBoolean(false);
    when(mockNode.getIsBusy()).thenReturn(isBusy);
    when(mockNode.createMusic(any(MusicCreationQueueItem.class))).thenReturn(result);

    musicCreateService.addToQueue(new MusicCreationQueueItem(diaryId, "pop", "테스트 가사"));

    // Then
    verify(diaryRepository, timeout(1000)).findById(diaryId);

    ArgumentCaptor<MusicFile> musicFileCaptor = ArgumentCaptor.forClass(MusicFile.class);
    verify(attachedFileRepository, timeout(1000)).save(musicFileCaptor.capture());

    MusicFile capturedFile = musicFileCaptor.getValue();
    assertEquals(1024L, capturedFile.getSize());
    assertEquals(diaryId, capturedFile.getDiary().getId());

    verify(mockDiary, timeout(1000)).setMusicFile(any(MusicFile.class));
    verify(diaryRepository, timeout(1000)).save(mockDiary);
  }

  @Test
  void handleMusicCreationResult_ShouldHandleNonExistingDiary() throws IOException {
    // Given
    Long diaryId = 1L;
    byte[] musicData = "test music data".getBytes();
    InputStream is = new ByteArrayInputStream(musicData);

    MusicCreationResult result = new MusicCreationResult(diaryId, is);

    when(diaryRepository.findById(diaryId)).thenReturn(Optional.empty());

    // When - indirectly calling handleMusicCreationResult
    AtomicBoolean isBusy = new AtomicBoolean(false);
    when(mockNode.getIsBusy()).thenReturn(isBusy);
    when(mockNode.createMusic(any(MusicCreationQueueItem.class))).thenReturn(result);

    musicCreateService.addToQueue(new MusicCreationQueueItem(diaryId, "pop", "테스트 가사"));

    // Then
    verify(diaryRepository, timeout(1000)).findById(diaryId);
    verify(fileSystemHandler, never()).saveStream(any(), any());
    verify(attachedFileRepository, never()).save(any());
  }
}