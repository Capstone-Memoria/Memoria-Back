package ac.mju.memoria.backend.domain.user.controller;

import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Getter @Setter
@RequestMapping("/api/user/{userEmail}")
public class UserController {
    private final UserService userService;

    @PatchMapping
    public ResponseEntity<UserDto.UserResponse> updateProfile(
            @PathVariable String userEmail,
            @RequestBody UserDto.UserUpdateRequest request
    ) {
        UserDto.UserResponse updatedUser = userService.updateProfile(userEmail, request);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<UserDto.UserResponse> getProfile(
            @PathVariable String userEmail
    ) {
        UserDto.UserResponse user = userService.getProfile(userEmail);

        return ResponseEntity.ok(user);
    }
}
