package ac.mju.memoria.backend.system.security.exception;

public class JwtTokenExpiredException extends JwtAuthenticationException {
    public JwtTokenExpiredException() {
        super("Token has expired", 401);
    }

    public JwtTokenExpiredException(Throwable cause) {
        super("Token has expired", 401, cause);
    }
}
