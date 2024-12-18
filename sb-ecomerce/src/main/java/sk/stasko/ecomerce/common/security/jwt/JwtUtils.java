package sk.stasko.ecomerce.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import sk.stasko.ecomerce.common.properties.JwtProperties;
import sk.stasko.ecomerce.common.security.service.UserDetailsImpl;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {
    
    private final JwtProperties jwtProperties;

    public String getJwtFromCoockie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtProperties.getJwtCookieName());
        return cookie != null ? cookie.getValue() : null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails) {
        String jwt = generateTokenFromUsername(userDetails.getUsername());
        return ResponseCookie.from(jwtProperties.getJwtCookieName(), jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtProperties.getJwtCookieName()).path("/api").build();
    }
    
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove Bearer prefix
        }
        return null;
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtProperties.getJwtExpirationMs()))
                .signWith(key())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                        .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getJwtSecret());
        if (keyBytes.length < 32) { // Ensure the key is at least 256 bits
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits");
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getJwtSecret()));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
