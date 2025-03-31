package ac.mju.memoria.backend.domain.auth.controller;

import ac.mju.memoria.backend.domain.auth.dto.AuthDto;
import ac.mju.memoria.backend.domain.auth.service.AuthService;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthDto.SignInResponse signIn(@RequestBody AuthDto.SignInRequest request) {
        return authService.signIn(request);
    }

    @GetMapping("/email-exist")
    public AuthDto.EmailExistResponse checkEmail(@RequestBody AuthDto.EmailExistRequest request) {
        return authService.checkEmailExist(request);
    }

    @PostMapping("/register")
    public UserDto.UserResponse signUp(@RequestBody AuthDto.SignUpRequest request) {
        return authService.signUp(request);
    }

    @GetMapping("/me")
    public UserDto.UserResponse whoami(
            @AuthenticationPrincipal UserDetails user
    ) {
        return UserDto.UserResponse.from(
                user.getUser()
        );
    }
}
