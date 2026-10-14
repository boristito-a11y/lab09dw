package pe.edu.tecsup.lab09.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.tecsup.lab09.models.Usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMinutos;

    /** @Value lee application.properties: la clave no se escribe en el código. */
    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMinutos = expiracionMinutos;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getUsername())                         // claim sub
                .claim("rol", usuario.getRol().name())                  // claim personalizado
                .issuedAt(Date.from(ahora))                             // claim iat
                .expiration(Date.from(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES)))
                .signWith(clave)                                        // firma HS256
                .compact();
    }

    /** Verifica firma y expiración. Si algo falla lanza JwtException. */
    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }
}
