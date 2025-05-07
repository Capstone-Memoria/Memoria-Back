package ac.mju.memoria.backend.domain.ai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.model.MusicCreationQueueItem;
import ac.mju.memoria.backend.domain.ai.service.MusicCreateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

  private final MusicCreateService musicCreateService;

  @PostMapping("/generate-music-sync")
  public ResponseEntity<String> generateMusicSync(@RequestBody MusicDto.CreateRequestSync request) {
    MusicCreationQueueItem queueItem = new MusicCreationQueueItem(
        1L,
        request.getGenre_txt(),
        request.getLyrics_txt());

    musicCreateService.addToQueue(queueItem);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body("Music creation request accepted. Processing in background.");
  }

  @GetMapping("/status")
  public ResponseEntity<MusicDto.StatusResponse> getStatus() {
    boolean isGenerating = musicCreateService.isAnyNodeBusy();
    boolean available = true;

    MusicDto.StatusResponse status = MusicDto.StatusResponse.builder()
        .isGeneratingMusic(isGenerating)
        .available(available)
        .build();

    return ResponseEntity.ok(status);
  }
}