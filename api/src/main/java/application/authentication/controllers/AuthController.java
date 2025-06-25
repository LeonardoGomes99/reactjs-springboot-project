package application.authentication.controllers;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import application.authentication.config.user.CustomUserDetailsService;
import application.authentication.config.jwt.JwtUtil;
import application.authentication.dto.LoginRequest;
import application.authentication.responses.ValidationResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping
public class AuthController {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long jwtRefreshExpiration;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader,HttpServletRequest request) {
        try {
            // 1. Extrair o token do cookie
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            // 2. Verificar se o token foi encontrado
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ValidationResponse(false, null, null, null));
            }
            // 3. Extrair e verificar a data de expiração
            Date expirationDate;
            try {
                expirationDate = jwtUtil.extractExpiration(token);
                if (expirationDate == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ValidationResponse(false, null, null, null));
                }

                // Comparar com a data atual do sistema
                Date currentDate = new Date();
                if (expirationDate.before(currentDate)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ValidationResponse(false, null, null, null));
                }

            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ValidationResponse(false, null, null, null));
            }
            // 4. Extrair o username
            String username;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ValidationResponse(false, null, null, null));
            }
            // 5. Verificar se o username é válido
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ValidationResponse(false, null, null, null));
            }
            // 6. Carregar os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // 7. Validar o token
            if (jwtUtil.validateToken(token, userDetails)) {
                // Formatar a data de expiração para o response
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedExpiration = formatter.format(expirationDate);
                return ResponseEntity.ok(new ValidationResponse(true, username, jwtUtil.extractRoles(token), formattedExpiration));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ValidationResponse(false, null, null, null));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidationResponse(false, null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ValidationResponse(false, null, null, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Token de acesso (expira em ex: 15 min)
            String accessToken = jwtUtil.generateToken(userDetails);
            // Refresh token (expira em ex: 7 dias)
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            // Cookie do token de acesso
            Cookie tokenCookie = new Cookie("token", accessToken);
            tokenCookie.setHttpOnly(true);
            tokenCookie.setSecure(false); // true se usar HTTPS
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge((int) (jwtExpiration / 1000)); // 15 min
            // Cookie do refresh token
            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // true se usar HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias
            response.addCookie(tokenCookie);
            response.addCookie(refreshCookie);
            return ResponseEntity.ok("OK");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Extrai o refresh token do cookie
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refresh_token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token não encontrado");
            }
            // Valida o refresh token
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
            }
            // Obtém o username do refresh token
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            // Carrega os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Gera novo token JWT
            String newJwt = jwtUtil.generateToken(userDetails);
            // Cria novo cookie HTTP-only para o access token
            Cookie accessTokenCookie = new Cookie("token", newJwt);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge((int) (jwtExpiration / 1000));
            // Opcional: Gera novo refresh token
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            Cookie refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (jwtRefreshExpiration / 1000));
            // Adiciona os cookies à resposta
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao atualizar token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Remove token de acesso
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(false); // mantenha igual ao login
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0); // expira imediatamente
        // Remove refresh token
        Cookie refreshCookie = new Cookie("refresh_token", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // mantenha igual ao login
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // expira imediatamente
        // Envia ambos para o navegador
        response.addCookie(tokenCookie);
        response.addCookie(refreshCookie);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}

