package ac.mju.memoria.backend.domain.diarybook.controller;

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
public class AICharacterController {

    private final AICharacterService aiCharacterService;

    @PostMapping
    private ResponseEntity<AICharacterDto.AICharacterResponse> createAICharacter(
            @PathVariable Long diaryBookId,
            @RequestBody AICharacterDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .createAICharacter(request, diaryBookId, userDetails);
        return ResponseEntity.ok(character);
    }

    @PatchMapping("/{characterId}")
    private ResponseEntity<AICharacterDto.AICharacterResponse> updateAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @RequestBody AICharacterDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .updateAICharacter(request, diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(character);
    }

    @GetMapping("/{characterId}")
    private ResponseEntity<AICharacterDto.AICharacterResponse> getAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @AuthenticationPrincipal UserDetails userDetails) {

        AICharacterDto.AICharacterResponse character = aiCharacterService
                .getAICharacter(diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(character);
    }

    @GetMapping
    private ResponseEntity<List<AICharacterDto.AICharacterResponse>> getAllAICharacters(
            @PathVariable Long diaryBookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<AICharacterDto.AICharacterResponse> characters = aiCharacterService
                .getAllAICharacters(diaryBookId, userDetails);
        return ResponseEntity.ok(characters);
    }

    @DeleteMapping("/{characterId}")
    private ResponseEntity<Void> deleteAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @AuthenticationPrincipal UserDetails userDetails) {

        aiCharacterService.deleteAICharacter(diaryBookId, characterId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
