package application.authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
public class JwtUtil {
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;

    @Value("${jwt.refresh.expiration}")
    private Long REFRESH_EXPIRATION_TIME;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        logger.info(String.format("JWT inicializado. Chave secreta carregada. Tempo de expiração: %d ms (%.1f horas)",EXPIRATION_TIME, EXPIRATION_TIME / 3600000.0));

    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());
        String token = createToken(claims, userDetails.getUsername());
        logger.fine(String.format("Token gerado para usuário %s com expiração em %d ms",
                userDetails.getUsername(), EXPIRATION_TIME));
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (JwtException e) {
            logger.severe("Erro ao extrair data de expiração do token: " + e.getMessage());
            throw new JwtException("Erro ao extrair data de expiração: " + e.getMessage());
        }
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractAllClaimsPublic(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.fine(String.format("Validação do token para usuário %s: %s",
                    username, isValid ? "Válido" : "Inválido"));
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warning("Erro ao validar token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        boolean isExpired = extractAllClaims(token).getExpiration().before(new Date());
        if (isExpired) {
            logger.warning(String.format("Token expirado. Data de expiração: %s", extractExpiration(token)));
        }
        return isExpired;
    }

    // Gera um refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh"); // Indica que é um refresh token
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList());
        String token = createRefreshToken(claims, userDetails.getUsername());
        logger.fine(String.format("Refresh token gerado para usuário %s com expiração em %d ms",
                userDetails.getUsername(), REFRESH_EXPIRATION_TIME));
        return token;
    }

    // Cria o refresh token com tempo de expiração específico
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Obtém o username do token (já existe como extractUsername, mas renomeado para consistência com o endpoint)
    public String getUsernameFromToken(String token) {
        try {
            return extractUsername(token);
        } catch (JwtException e) {
            logger.severe("Erro ao extrair username do refresh token: " + e.getMessage());
            throw new JwtException("Refresh token inválido: " + e.getMessage());
        }
    }

    // Valida o refresh token
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            // Verifica se é um refresh token
            if (!"refresh".equals(claims.get("type"))) {
                logger.warning("Token não é um refresh token");
                return false;
            }
            // Verifica expiração
            boolean isValid = !isTokenExpired(token);
            logger.fine(String.format("Validação do refresh token: %s",
                    isValid ? "Válido" : "Inválido"));
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.severe("Erro ao validar refresh token: " + e.getMessage());
            return false;
        }
    }
}