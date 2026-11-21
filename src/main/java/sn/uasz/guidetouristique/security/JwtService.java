package sn.uasz.guidetouristique.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sn.uasz.guidetouristique.domain.Role;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = props.expirationMinutes();
    }

    public long expirationMinutes() {
        return expirationMinutes;
    }

    public String generer(String username, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("roles", List.of(role.name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /** Valide la signature et l'expiration ; leve JwtException si le jeton est invalide. */
    public Authentication lire(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        List<?> roles = claims.get("roles", List.class);
        List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                : roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }
}
