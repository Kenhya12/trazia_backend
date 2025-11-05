package com.trazia.trazia_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "TraziaSecretKeyForJWTTokenGeneration2025MustBeLongEnoughForHS512AlgorithmSecurityPurposes";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final long JWT_EXPIRATION = 86400000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        String token = buildToken(extraClaims, userDetails, JWT_EXPIRATION);
        log.info("🔐 TOKEN GENERATED for user: {}", userDetails.getUsername());
        log.info("🔐 Generated token: {}", token);
        return token;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        // Usar el email como subject si es nuestra entidad User personalizada
        String subject;
        if (userDetails instanceof com.trazia.trazia_project.entity.user.User) {
            subject = ((com.trazia.trazia_project.entity.user.User) userDetails).getEmail();
        } else {
            subject = userDetails.getUsername();
        }

        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(subject) // ← Ahora usa email para usuarios personalizados
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();

        log.info("🔐 TOKEN BUILT - Subject: {}, Expiration: {}ms", subject, expiration);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String usernameFromToken = extractUsername(token);

        // Obtener el username REAL del UserDetails (que podría ser email o username)
        String usernameFromUserDetails;
        if (userDetails instanceof com.trazia.trazia_project.entity.user.User) {
            // Para nuestra entidad User, usar el email para comparar
            usernameFromUserDetails = ((com.trazia.trazia_project.entity.user.User) userDetails).getEmail();
        } else {
            usernameFromUserDetails = userDetails.getUsername();
        }

        boolean usernameMatch = usernameFromToken.equals(usernameFromUserDetails);
        boolean notExpired = !isTokenExpired(token);
        boolean valid = usernameMatch && notExpired;

        log.info("🔐 TOKEN VALIDATION DEBUG:");
        log.info("🔐   Token: {}", token);
        log.info("🔐   Username from token: '{}'", usernameFromToken);
        log.info("🔐   Username from UserDetails: '{}'", usernameFromUserDetails);
        log.info("🔐   Username match: {}", usernameMatch);
        log.info("🔐   Token expired: {}", !notExpired);
        log.info("🔐   Overall valid: {}", valid);

        return valid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.info("🔐 Token EXPIRED - Expiration: {}", extractExpiration(token));
        }
        return expired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("🔐 ERROR parsing token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }

    public String getUsernameFromToken(String token) {
        try {
            return extractUsername(token);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
}