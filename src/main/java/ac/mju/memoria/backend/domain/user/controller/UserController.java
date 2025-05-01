package ac.mju.memoria.backend.domain.user.controller;

import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Getter @Setter // TODO: Controller에 @Getter @Setter 사용 지양, 필요시 제거
@RequestMapping("/api/user/{userEmail}")
@Tag(name = "User", description = "사용자 프로필 API")
public class UserController {
    private final UserService userService;

    @PatchMapping
    @Operation(summary = "사용자 프로필 수정", description = "사용자의 닉네임 또는 프로필 이미지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공")
    public ResponseEntity<UserDto.UserResponse> updateProfile(
            @Parameter(description = "수정할 사용자의 이메일") @PathVariable String userEmail,
            @RequestBody UserDto.UserUpdateRequest request
    ) {
        UserDto.UserResponse updatedUser = userService.updateProfile(userEmail, request);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    public ResponseEntity<UserDto.UserResponse> getProfile(
            @Parameter(description = "조회할 사용자의 이메일") @PathVariable String userEmail
    ) {
        UserDto.UserResponse user = userService.getProfile(userEmail);

        return ResponseEntity.ok(user);
    }
}
