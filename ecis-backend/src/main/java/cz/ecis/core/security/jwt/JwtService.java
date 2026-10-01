package cz.ecis.core.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import cz.ecis.core.settings.EcisJwtSettings;
import cz.ecis.db.ent.UserEnt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String SESSION_ID_CLAIM_KEY = "session";
    public static final String PASSWORD_CHANGED_AT_CLAIM_KEY = "pwd_changed_at";

    public final EcisJwtSettings ecisJwtSettings;

    public String generateToken(UserEnt user, JwtType jwtType, String sessionId) {
        Key key;
        Integer validity;
        switch (jwtType) {
            case JwtType.ACCESS -> {
                key = this.getAccessSigningKey();
                validity = this.getAccessValidity();
            }
            case JwtType.REFRESH -> {
                key = this.getRefreshSigningKey();
                validity = this.getRefreshValidity();
            }
            default -> throw new IllegalArgumentException("Unimplemented JWT type");
        }

        return Jwts.builder()
            .subject(user.getUsername())
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + (validity*1000))
            )
            .signWith(key)
            .claim(SESSION_ID_CLAIM_KEY, sessionId)
            .claim(PASSWORD_CHANGED_AT_CLAIM_KEY, user.getPasswordChanged().toInstant().getEpochSecond())
            .compact();
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public String extractSessionId(Claims claims) {
        return claims.get(SESSION_ID_CLAIM_KEY, String.class);
    }

    public OffsetDateTime extractPasswordChanged(Claims claims) {
        long epoch = claims.get(PASSWORD_CHANGED_AT_CLAIM_KEY, Long.class);
        return Instant.ofEpochSecond(epoch).atOffset(ZoneOffset.UTC);
    }

    /**
     * Validuje token a vrací jeho claims. Pokud je token neplatný, vyhodí IllegalArgumentException.
     * Metodu lze použít pouze pro validaci tokenu, ne pro získání dat z tokenu. Pro získání dat z tokenu použijte metodu extractClaims.
     * @param token - token k validaci
     * @param jwtType - typ tokenu (ACCESS nebo REFRESH)
     * @return Claims - claims tokenu
     * @throws IllegalArgumentException - pokud je token neplatný
     */
    public Claims extractClaims(String token, JwtType jwtType) {
        Key key;
        switch (jwtType) {
            case JwtType.ACCESS -> {
                key = this.getAccessSigningKey();
            }
            case JwtType.REFRESH -> {
                key = this.getRefreshSigningKey();
            }
            default -> throw new IllegalArgumentException("Unimplemented JWT type");
        }
        return Jwts.parser()
            .verifyWith((SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    }

    public boolean isTokenValid(Claims claims, String username) {
        return this.extractUsername(claims).equals(username);
    }

    private Key getAccessSigningKey() {
        return Keys.hmacShaKeyFor(this.ecisJwtSettings.access().secretKey().getBytes(StandardCharsets.UTF_8));
    }

    private Key getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(this.ecisJwtSettings.refresh().secretKey().getBytes(StandardCharsets.UTF_8));
    }

    private Integer getAccessValidity() {
        return this.ecisJwtSettings.access().validity();
    }

    private Integer getRefreshValidity() {
        return this.ecisJwtSettings.refresh().validity();
    }

    public enum JwtType {
        REFRESH, ACCESS;
    }
}