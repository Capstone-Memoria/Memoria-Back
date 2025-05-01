package ac.mju.memoria.backend.domain.auth.controller;

import ac.mju.memoria.backend.domain.auth.dto.AuthDto;
import ac.mju.memoria.backend.domain.auth.service.AuthService;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증/인가 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    public AuthDto.SignInResponse signIn(@RequestBody @Valid AuthDto.SignInRequest request) {
        return authService.signIn(request);
    }

    @GetMapping("/email-exist")
    @Operation(summary = "이메일 중복 확인", description = "입력된 이메일의 사용 가능 여부를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 확인 성공")
    public AuthDto.EmailExistResponse checkEmail(@RequestBody @Valid AuthDto.EmailExistRequest request) {
        return authService.checkEmailExist(request);
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    public UserDto.UserResponse signUp(@RequestBody @Valid AuthDto.SignUpRequest request) {
        return authService.signUp(request);
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    public UserDto.UserResponse whoami(
            @Parameter(hidden = true) // Swagger UI에서 해당 파라미터 숨김 처리
            @AuthenticationPrincipal UserDetails user
    ) {
        return UserDto.UserResponse.from(
                user.getUser()
        );
    }
}
