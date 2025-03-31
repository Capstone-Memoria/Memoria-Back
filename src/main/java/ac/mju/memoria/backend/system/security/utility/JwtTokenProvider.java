package ac.mju.memoria.backend.system.security.utility;

import ac.mju.memoria.backend.system.security.model.AuthDetails;
import ac.mju.memoria.backend.system.security.model.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Key secret;

    public JwtDto.TokenData issueToken(AuthDetails authDetails, Long expireHours) {
        Claims claims = Jwts.claims().setSubject(authDetails.getName());

        LocalDateTime expireLocalDateTime = LocalDateTime.now().plusHours(expireHours);

        String tokenString = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expireLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secret)
                .compact();

        return JwtDto.TokenData.builder()
                .tokenString(tokenString)
                .expireAt(expireLocalDateTime)
                .build();
    }
}
