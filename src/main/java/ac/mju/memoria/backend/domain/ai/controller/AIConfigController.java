package ac.mju.memoria.backend.domain.ai.controller;

import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import ac.mju.memoria.backend.domain.ai.service.AiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/config")
@Tag(name = "AIConfig", description = "AI 설정 API")
public class AIConfigController {
  private final AiConfigService aiConfigService;

  @GetMapping("/image/nodes")
  @Operation(summary = "이미지 생성 노드 목록 조회", description = "현재 등록된 이미지 생성 AI 노드 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "이미지 노드 목록 조회 성공")
  public ResponseEntity<List<NodeDto.InfoResponse>> getImageNodes() {
    return ResponseEntity.ok(aiConfigService.getImageNodes());
  }

  @GetMapping("/music/nodes")
  @Operation(summary = "음악 생성 노드 목록 조회", description = "현재 등록된 음악 생성 AI 노드 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "음악 노드 목록 조회 성공")
  public ResponseEntity<List<NodeDto.InfoResponse>> getMusicNodes() {
    return ResponseEntity.ok(aiConfigService.getMusicNodes());
  }

  @PostMapping("/image/nodes")
  @Operation(summary = "이미지 생성 노드 추가", description = "새로운 이미지 생성 AI 노드를 추가합니다.")
  @ApiResponse(responseCode = "200", description = "이미지 노드 추가 성공")
  public ResponseEntity<Void> addImageNode(@RequestBody NodeDto.CreateRequest request) {
    aiConfigService.addImageNode(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/music/nodes")
  @Operation(summary = "음악 생성 노드 추가", description = "새로운 음악 생성 AI 노드를 추가합니다.")
  @ApiResponse(responseCode = "200", description = "음악 노드 추가 성공")
  public ResponseEntity<Void> addMusicNode(@RequestBody NodeDto.CreateRequest request) {
    aiConfigService.addMusicNode(request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/image/nodes/{url}")
  @Operation(summary = "이미지 생성 노드 삭제", description = "등록된 이미지 생성 AI 노드를 삭제합니다.")
  @ApiResponse(responseCode = "200", description = "이미지 노드 삭제 성공")
  public ResponseEntity<Void> deleteImageNode(
      @Parameter(description = "삭제할 노드의 URL", example = "http://localhost:8001") @PathVariable String url) {
    aiConfigService.deleteImageNode(url);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/music/nodes/{url}")
  @Operation(summary = "음악 생성 노드 삭제", description = "등록된 음악 생성 AI 노드를 삭제합니다.")
  @ApiResponse(responseCode = "200", description = "음악 노드 삭제 성공")
  public ResponseEntity<Void> deleteMusicNode(
      @Parameter(description = "삭제할 노드의 URL", example = "http://localhost:8002") @PathVariable String url) {
    aiConfigService.deleteMusicNode(url);
    return ResponseEntity.ok().build();
  }
}
