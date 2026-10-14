package pe.edu.tecsup.lab09.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/** No lleva @Component: se crea en SecurityConfig para que no se registre dos veces. */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO)) {
            String token = header.substring(PREFIJO.length());
            try {
                Claims claims = jwtService.validar(token);
                String username = claims.getSubject();
                String rol = claims.get("rol", String.class);

                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                var auth = new UsernamePasswordAuthenticationToken(
                        username, null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException e) {
                // Firma alterada, token expirado o mal formado: se continúa como anónimo
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);   // siempre se continúa con la cadena
    }
}
