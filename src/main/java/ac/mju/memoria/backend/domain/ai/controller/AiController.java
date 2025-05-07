package ac.mju.memoria.backend.domain.ai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.model.MusicCreationQueueItem;
import ac.mju.memoria.backend.domain.ai.service.MusicCreateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai") // You might want to adjust the base path
@RequiredArgsConstructor
public class AiController {

  private final MusicCreateService musicCreateService;

  @PostMapping("/generate-music-sync")
  public ResponseEntity<String> generateMusicSync(@RequestBody MusicDto.CreateRequestSync request) {
    // Create a queue item from the request DTO
    MusicCreationQueueItem queueItem = new MusicCreationQueueItem(
        // You need to determine the diaryId here. Assuming it comes from the request or
        // context.
        // For now, using a placeholder or generating a temporary one.
        // If diaryId should be generated after successful creation, you might need a
        // different approach.
        // Assuming for now that diaryId is part of the request or context handled
        // elsewhere.
        // Placeholder diaryId:
        1L, // Placeholder: Replace with actual diaryId logic
        request.getGenre_txt(),
        request.getLyrics_txt());

    // Add the item to the queue for asynchronous processing
    musicCreateService.addToQueue(queueItem);

    // Return an immediate response indicating that the request has been accepted
    // and is being processed.
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body("Music creation request accepted. Processing in background.");
  }

  @GetMapping("/music/download/{file_id}")
  public ResponseEntity<?> downloadMusic(@PathVariable("file_id") String fileId) {
    // This is a placeholder. Actual implementation needs to serve the file.
    // You will need logic here to retrieve the file based on fileId and return it
    // as a downloadable resource.
    // Example placeholder response:
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
        .body("Download functionality not implemented yet for file ID: " + fileId);
  }

  @GetMapping("/status")
  public ResponseEntity<MusicDto.StatusResponse> getStatus() {
    // Check the status using the service method
    boolean isGenerating = musicCreateService.isAnyNodeBusy();

    // Assuming the service is available if the controller and service are running.
    // You might want a more sophisticated check here.
    boolean available = true; // Placeholder: Assuming service is available if running

    MusicDto.StatusResponse status = MusicDto.StatusResponse.builder()
        .isGeneratingMusic(isGenerating)
        .available(available)
        .build();

    return ResponseEntity.ok(status);
  }
}