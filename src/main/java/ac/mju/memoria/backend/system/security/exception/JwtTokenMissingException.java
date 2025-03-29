package ac.mju.memoria.backend.system.security.exception;

public class JwtTokenMissingException extends JwtAuthenticationException {
    public JwtTokenMissingException() {
        super("Token is missing", 401);
    }

    public JwtTokenMissingException(Throwable cause) {
        super("Token is missing", 401, cause);
    }
}
