package application.authentication.service;

import application.authentication.config.jwt.JwtUtil;
import application.authentication.dto.LoginRequest;
import application.authentication.responses.ValidationResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import application.authentication.config.user.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AuthService {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long jwtRefreshExpiration;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;


    public ResponseEntity<ValidationResponse> validate(HttpServletRequest request) {
        try {
            String token = extractCookie(request, "token");
            if (token == null || token.isEmpty()) return unauthorized();

            Date expiration = jwtUtil.extractExpiration(token);
            if (expiration == null || expiration.before(new Date())) return unauthorized();

            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isEmpty()) return unauthorized();

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiration);
                return ResponseEntity.ok(new ValidationResponse(true, username, jwtUtil.extractRoles(token), formatted));
            }

            return unauthorized();

        } catch (UsernameNotFoundException e) {
            return unauthorized();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ValidationResponse(false, null, null, null));
        }
    }

    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            response.addCookie(createCookie("token", accessToken, jwtExpiration));
            response.addCookie(createCookie("refresh_token", refreshToken, jwtRefreshExpiration));

            return ResponseEntity.ok("OK");

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = extractCookie(request, "refresh_token");
            if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token não encontrado");

            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
            }

            String username = jwtUtil.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String newJwt = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            response.addCookie(createCookie("token", newJwt, jwtExpiration));
            response.addCookie(createCookie("refresh_token", newRefreshToken, jwtRefreshExpiration));

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao atualizar token");
        }
    }

    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(expireCookie("token"));
        response.addCookie(expireCookie("refresh_token"));
        return ResponseEntity.noContent().build(); // 204
    }

    // Funções Auxiliares
    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }

    private Cookie createCookie(String name, String value, long durationMs) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ou true se usar HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (durationMs / 1000));
        return cookie;
    }

    private Cookie expireCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // manter igual ao login
        cookie.setPath("/");
        cookie.setMaxAge(0); // expira imediatamente
        return cookie;
    }

    private ResponseEntity<ValidationResponse> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ValidationResponse(false, null, null, null));
    }

}
