package ac.mju.memoria.backend.domain.diarybook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diarybook.dto.AICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.service.AICharacterService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/custom-characters")
@Tag(name = "AICharacter", description = "커스텀 AI 캐릭터 API")
public class AICharacterController {

    private final AICharacterService aiCharacterService;

    @PostMapping
    @Operation(summary = "커스텀 AI 캐릭터 생성", description = "커스텀 AI 캐릭터를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "커스텀 AI 캐릭터 생성 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> createAICharacter(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @RequestBody @Valid AICharacterDto.CreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .createAICharacter(request, diaryBookId, userDetails);
        return ResponseEntity.ok(character);
    }

    @PatchMapping("/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 수정", description = "커스텀 AI 캐릭터를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 AI 캐릭터 수정 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> updateAICharacter(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @RequestBody @Valid AICharacterDto.UpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .updateAICharacter(request, diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 조회", description = "특정 커스텀 AI 캐릭터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 AI 캐릭터 조회 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> getAICharacter(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .getAICharacter(diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(character);
    }

    @GetMapping
    @Operation(summary = "모든 커스텀 AI 캐릭터 조회", description = "특정 다이어리 북의 모든 커스텀 AI 캐릭터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 커스텀 AI 캐릭터 조회 성공")
    private ResponseEntity<List<AICharacterDto.AICharacterResponse>> getAllAICharacters(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<AICharacterDto.AICharacterResponse> characters = aiCharacterService
                .getAllAICharacters(diaryBookId, userDetails);
        return ResponseEntity.ok(characters);
    }

    @DeleteMapping("/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 삭제", description = "특정 커스텀 AI 캐릭터를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "커스텀 AI 캐릭터 삭제 성공")
    private ResponseEntity<Void> deleteAICharacter(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        aiCharacterService.deleteAICharacter(diaryBookId, characterId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
