package application.authentication.controllers;

import application.authentication.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private AuthService authService;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(HttpServletRequest request) {
        return authService.validate(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return authService.logout(response);
    }
}

