package ac.mju.memoria.backend.domain.ai.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.dto.QueueDto;
import ac.mju.memoria.backend.domain.ai.service.AiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/config")
@Tag(name = "AIConfig", description = "AI 설정 API")
public class AIConfigController {
  private final AiConfigService aiConfigService;

  @PostMapping("/nodes")
  @Operation(summary = "AI 노드 생성", description = "새로운 AI 노드를 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "AI 노드 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (URL 중복 등)")
  })
  public ResponseEntity<NodeDto.Response> createNode(
      @Valid @RequestBody NodeDto.CreateRequest request) {
    NodeDto.Response response = aiConfigService.createNode(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/nodes")
  @Operation(summary = "AI 노드 목록 조회", description = "등록된 모든 AI 노드 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "AI 노드 목록 조회 성공")
  public ResponseEntity<List<NodeDto.Response>> getAllNodes() {
    List<NodeDto.Response> response = aiConfigService.getAllNodes();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/nodes/{nodeId}")
  @Operation(summary = "AI 노드 수정", description = "기존 AI 노드의 정보를 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "AI 노드 수정 성공"),
      @ApiResponse(responseCode = "404", description = "AI 노드를 찾을 수 없음"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (URL 중복 등)")
  })
  public ResponseEntity<NodeDto.Response> updateNode(
      @Parameter(description = "노드 ID", required = true) @PathVariable Long nodeId,
      @Valid @RequestBody NodeDto.UpdateRequest request) {
    NodeDto.Response response = aiConfigService.updateNode(nodeId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/nodes/{nodeId}")
  @Operation(summary = "AI 노드 삭제", description = "특정 AI 노드를 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "AI 노드 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "AI 노드를 찾을 수 없음")
  })
  public ResponseEntity<Void> deleteNode(
      @Parameter(description = "노드 ID", required = true) @PathVariable Long nodeId) {
    aiConfigService.deleteNode(nodeId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/nodes/queue-status")
  @Operation(summary = "AI 노드 큐 상태 조회", description = "Image 및 Music 노드 풀의 현재 큐 상태를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "AI 노드 큐 상태 조회 성공")
  public ResponseEntity<NodeDto.QueueStatusResponse> getQueueStatus() {
    int imageQueueSize = aiConfigService.getImageNodeQueueSize();
    int musicQueueSize = aiConfigService.getMusicNodeQueueSize();
    int musicPendingJobs = aiConfigService.getMusicNodePendingJobsCount();
    return ResponseEntity.ok(new NodeDto.QueueStatusResponse(imageQueueSize, musicQueueSize, musicPendingJobs));
  }

  @GetMapping("/music/queue")
  @Operation(summary = "뮤직 큐 목록 조회", description = "뮤직 노드 풀의 현재 대기 및 처리 중인 큐 아이템 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "뮤직 큐 목록 조회 성공")
  public ResponseEntity<QueueDto.QueueListResponse> getMusicQueue() {
    QueueDto.QueueListResponse response = aiConfigService.getMusicQueueStatus();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/music/queue/reorder")
  @Operation(summary = "뮤직 큐 아이템 순서 변경", description = "뮤직 큐에서 특정 아이템의 순서를 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "큐 아이템 순서 변경 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (존재하지 않는 UUID 또는 잘못된 위치)")
  })
  public ResponseEntity<String> reorderMusicQueueItem(
      @Valid @RequestBody QueueDto.ReorderQueueRequest request) {
    boolean success = aiConfigService.reorderMusicQueueItem(request.getItemUuid(), request.getNewPosition());
    if (success) {
      return ResponseEntity.ok("큐 아이템 순서가 성공적으로 변경되었습니다.");
    } else {
      return ResponseEntity.badRequest().body("큐 아이템 순서 변경에 실패했습니다. UUID 또는 위치를 확인해주세요.");
    }
  }

  @DeleteMapping("/music/queue/{itemUuid}")
  @Operation(summary = "뮤직 큐 아이템 삭제", description = "뮤직 큐에서 특정 아이템을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "큐 아이템 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "큐 아이템을 찾을 수 없음")
  })
  public ResponseEntity<QueueDto.DeleteQueueItemResponse> deleteMusicQueueItem(
      @Parameter(description = "삭제할 큐 아이템 UUID", required = true) @PathVariable UUID itemUuid) {
    boolean success = aiConfigService.deleteMusicQueueItem(itemUuid);

    QueueDto.DeleteQueueItemResponse response = new QueueDto.DeleteQueueItemResponse();
    response.setSuccess(success);
    response.setDeletedItemUuid(itemUuid);

    if (success) {
      response.setMessage("큐 아이템이 성공적으로 삭제되었습니다.");
      return ResponseEntity.ok(response);
    } else {
      response.setMessage("큐 아이템을 찾을 수 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
