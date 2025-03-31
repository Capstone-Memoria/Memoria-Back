package ac.mju.memoria.backend.system.configuration;

import ac.mju.memoria.backend.domain.user.service.UserService;
import ac.mju.memoria.backend.system.security.configurer.JwtAutoConfigurerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAutoConfigurerFactory jwtAutoConfigurerFactory;
    private final UserService userService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        jwtAutoConfigurerFactory.create(userService)
                .pathConfigure((it) -> {
                    it.includeAll();
                    it.excludePath("/api/auth/register");
                    it.excludePath("/api/auth/login");
                    it.excludePath("/api/auth/email-exist");
                })
                .configure(httpSecurity);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화
    }
}
