package ac.mju.memoria.backend.system.security.configurer;

import ac.mju.memoria.backend.system.security.service.UserLoadService;
import ac.mju.memoria.backend.system.security.utility.JwtTokenProvider;
import ac.mju.memoria.backend.system.security.utility.JwtTokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class JwtAutoConfigurerFactory {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtTokenResolver jwtTokenResolver;

    public JwtAutoConfigurer create(UserLoadService userLoadService) {
        return new JwtAutoConfigurer(jwtTokenResolver, userLoadService, handlerExceptionResolver);
    }
}
