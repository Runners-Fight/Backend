package run.backend.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import run.backend.domain.auth.dto.response.TokenResponse;
import run.backend.global.exception.ApplicationException;
import run.backend.global.exception.ExceptionCode;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
        @Value ("${jwt.access-token-expire-time}") Long accessTokenExpireTime,
        @Value("${jwt.refresh-token-expire-time}") Long refreshTokenExpireTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public TokenResponse generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + this.accessTokenExpireTime);
        String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        Date refreshTokenExpiresIn = new Date(now + this.refreshTokenExpireTime);
        String refreshToken = Jwts.builder()
            .setSubject(authentication.getName())
            .setExpiration(refreshTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return new TokenResponse(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new ApplicationException(ExceptionCode.TOKEN_MISSING_AUTHORITY);
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String generateSignupToken(String providerId, String provider, String email, String name) {
        long now = (new Date()).getTime();
        return Jwts.builder()
            .setSubject(providerId)
            .claim("provider", provider)
            .claim("email", email)
            .claim("name", name)
            .setExpiration(new Date(now + 600000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public LocalDateTime getRefreshTokenExpiresAt(String refreshToken) {
        try {
            Claims claims = parseClaims(refreshToken);
            Date expiration = claims.getExpiration();
            return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now().plusSeconds(refreshTokenExpireTime / 1000);
        }
    }
}