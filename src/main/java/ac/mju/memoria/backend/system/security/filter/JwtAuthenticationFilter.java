package ac.mju.memoria.backend.system.security.filter;

import ac.mju.memoria.backend.system.security.exception.JwtAuthenticationException;
import ac.mju.memoria.backend.system.security.exception.JwtInvalidTokenException;
import ac.mju.memoria.backend.system.security.exception.JwtTokenMissingException;
import ac.mju.memoria.backend.system.security.service.UserLoadService;
import ac.mju.memoria.backend.system.security.utility.JwtTokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Properties For Path Matching
    private final List<String> ignorePatterns;
    private final List<String> allowedPatterns;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // Properties For User Loading & Exception Throwing
    private final JwtTokenResolver jwtTokenResolver;
    private final UserLoadService userLoadService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (this.isMatchingURI(servletPath)) {
            try {
                String token = jwtTokenResolver.parseTokenFromRequest(request)
                        .orElseThrow(JwtTokenMissingException::new);
                var parsedTokenData = jwtTokenResolver.resolveTokenFromString(token);

                var userDetails = userLoadService.loadUserByKey(parsedTokenData.getSubject());
                if(userDetails.isEmpty()) {
                    throw new JwtInvalidTokenException();
                }

                SecurityContextHolder.getContext()
                        .setAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        userDetails.get(),
                                        null,
                                        List.of(new SimpleGrantedAuthority("User"))
                                )
                        );
                filterChain.doFilter(request, response);
            }catch (Exception e) {
                if (e instanceof JwtAuthenticationException) {
                    handlerExceptionResolver.resolveException(request, response, null, e);
                } else {
                    handlerExceptionResolver.resolveException(request, response, null, new JwtAuthenticationException("Authentication failed", 401, e));
                }
            }
        }else{
            filterChain.doFilter(request, response);
        }
    }


    private Boolean isMatchingURI(String servletPath) {
        return this.allowedPatterns.stream().anyMatch((pattern) -> {
            return this.antPathMatcher.match(pattern, servletPath);
        }) && this.ignorePatterns.stream().noneMatch((pattern) -> {
            return this.antPathMatcher.match(pattern, servletPath);
        });
    }
}
