package ac.mju.memoria.backend.system.configuration;

import ac.mju.memoria.backend.domain.auth.dto.AuthDto;
import ac.mju.memoria.backend.domain.auth.service.AuthService;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final Environment environment;
    private final AuthService authService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        String[] activeProfiles = environment.getActiveProfiles();

        if (Arrays.stream(activeProfiles).toList().contains("local")) {
            AuthDto.SignUpRequest signUpRequest = AuthDto.SignUpRequest.builder()
                    .email("admin@test.com")
                    .password("root")
                    .nickName("admin")
                    .build();

            authService.signUp(signUpRequest);
        }
    }
}
