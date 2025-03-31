package ac.mju.memoria.backend.system.security.utility;

import ac.mju.memoria.backend.system.security.exception.JwtInvalidTokenException;
import ac.mju.memoria.backend.system.security.exception.JwtParseException;
import ac.mju.memoria.backend.system.security.exception.JwtTokenExpiredException;
import ac.mju.memoria.backend.system.security.model.ParsedTokenData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.ZoneId;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtTokenResolver {
    private final Key secret;

    public Optional<String> parseTokenFromRequest(HttpServletRequest request) {
        Optional<String> bearerToken;
        try {
            bearerToken = Optional.ofNullable(request.getHeader("Authorization"));
        }catch (Exception ignored) {
            bearerToken = Optional.empty();
        }

        return bearerToken
                .filter(token -> token.startsWith("Bearer"))
                .map(token -> token.substring(7));
    }

    public ParsedTokenData resolveTokenFromString(String tokenStr) {
        try {
            var parsed = Jwts.parserBuilder().setSigningKey(this.secret).build().parseClaimsJws(tokenStr);
            String subject = parsed.getBody().getSubject();
            return ParsedTokenData.builder()
                    .subject(subject)
                    .expireAt(parsed.getBody().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();
        }catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException(e);
        }catch (SignatureException e) {
            throw new JwtInvalidTokenException(e);
        }catch (Exception e) {
            throw new JwtParseException(e);
        }
    }
}
