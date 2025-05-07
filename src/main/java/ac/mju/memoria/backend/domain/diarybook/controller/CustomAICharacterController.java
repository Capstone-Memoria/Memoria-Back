package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.CustomAICharacterDto;
import ac.mju.memoria.backend.domain.diarybook.service.CustomAICharacterService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}")
public class CustomAICharacterController {

    private final CustomAICharacterService customAICharacterService;

    @PostMapping
    private ResponseEntity<CustomAICharacterDto.AICharacterResponse> createCustomAICharacter(
            @PathVariable Long diaryBookId,
            @RequestBody CustomAICharacterDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomAICharacterDto.AICharacterResponse custom = customAICharacterService
                .createCustomAICharacter(request, diaryBookId, userDetails);
        return ResponseEntity.ok(custom);
    }

    @PatchMapping("/characters/{characterId}")
    private ResponseEntity<CustomAICharacterDto.AICharacterResponse> updateCustomAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @RequestBody CustomAICharacterDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomAICharacterDto.AICharacterResponse custom = customAICharacterService
                .updateCustomAICharacter(request, diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(custom);
    }

    @GetMapping("/characters/{characterId}")
    private ResponseEntity<CustomAICharacterDto.AICharacterResponse> getCustomAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomAICharacterDto.AICharacterResponse custom = customAICharacterService
                .getCustomAICharacters(diaryBookId, characterId, userDetails);
        return ResponseEntity.ok(custom);
    }

    @DeleteMapping("/characters/{characterId}")
    private ResponseEntity<Void> deleteCustomAICharacter(
            @PathVariable Long diaryBookId, @PathVariable Long characterId,
            @AuthenticationPrincipal UserDetails userDetails) {

        customAICharacterService.deleteCustomAICharacter(diaryBookId, characterId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
