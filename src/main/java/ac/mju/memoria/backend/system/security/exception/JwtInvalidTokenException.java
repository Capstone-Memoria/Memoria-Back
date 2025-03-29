package ac.mju.memoria.backend.system.security.exception;


import lombok.Getter;

public class JwtInvalidTokenException extends JwtAuthenticationException {
    public JwtInvalidTokenException() {
        super("Invalid token", 401);
    }

    public JwtInvalidTokenException(Throwable cause) {
        super("Invalid token", 401, cause);
    }
}
