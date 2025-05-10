package ac.mju.memoria.backend.domain.diarybook.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ac.mju.memoria.backend.domain.diarybook.dto.AICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.service.AICharacterService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "AICharacter", description = "커스텀 AI 캐릭터 API")
public class AICharacterController {

    private final AICharacterService aiCharacterService;

    @PostMapping("/diary-book/{diaryBookId}/custom-characters")
    @Operation(summary = "커스텀 AI 캐릭터 생성", description = "커스텀 AI 캐릭터를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "커스텀 AI 캐릭터 생성 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> createAICharacter(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @ModelAttribute @Valid AICharacterDto.CreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .createAICharacter(request, diaryBookId, userDetails);
        return ResponseEntity.ok(character);
    }

    @PatchMapping("/custom-characters/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 수정", description = "커스텀 AI 캐릭터를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 AI 캐릭터 수정 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> updateAICharacter(
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @ModelAttribute @Valid AICharacterDto.UpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .updateAICharacter(request, characterId, userDetails);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/custom-characters/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 조회", description = "특정 커스텀 AI 캐릭터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "커스텀 AI 캐릭터 조회 성공")
    private ResponseEntity<AICharacterDto.AICharacterResponse> getAICharacter(
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService.getAICharacter(characterId, userDetails);

        return ResponseEntity.ok(character);
    }

    @GetMapping("/diary-book/{diaryBookId}/custom-characters")
    @Operation(summary = "모든 커스텀 AI 캐릭터 조회", description = "특정 다이어리 북의 모든 커스텀 AI 캐릭터를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 커스텀 AI 캐릭터 조회 성공")
    private ResponseEntity<List<AICharacterDto.AICharacterResponse>> getAllAICharacters(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<AICharacterDto.AICharacterResponse> characters = aiCharacterService
                .findAICharactersByDiaryBook(diaryBookId, userDetails);
        return ResponseEntity.ok(characters);
    }

    @DeleteMapping("/custom-characters/{characterId}")
    @Operation(summary = "커스텀 AI 캐릭터 삭제", description = "특정 커스텀 AI 캐릭터를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "커스텀 AI 캐릭터 삭제 성공")
    private ResponseEntity<Void> deleteAICharacter(
            @Parameter(description = "AI 캐릭터 ID") @PathVariable Long characterId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        aiCharacterService.deleteAICharacter(characterId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
