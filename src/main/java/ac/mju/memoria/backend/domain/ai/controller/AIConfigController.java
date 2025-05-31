package ac.mju.memoria.backend.domain.ai.controller;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.service.AiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
