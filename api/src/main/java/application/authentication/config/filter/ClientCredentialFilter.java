package application.authentication.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ClientCredentialFilter extends OncePerRequestFilter {

    @Value("${security.client.id}")
    private String clientId;

    @Value("${security.client.secret}")
    private String clientSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("/api/login".equals(request.getRequestURI())) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Client credentials ausentes");
                return;
            }

            try {
                String base64 = authHeader.substring(6);
                String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
                String[] parts = decoded.split(":", 2);

                if (parts.length != 2 || !clientId.equals(parts[0]) || !clientSecret.equals(parts[1])) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Client ID ou Secret inválidos");
                    return;
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro ao decodificar credenciais do client");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
