package pe.edu.tecsup.lab09.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import pe.edu.tecsup.lab09.security.JwtAuthFilter;
import pe.edu.tecsup.lab09.security.JwtService;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService)
            throws Exception {
        PathPatternRequestMatcher.Builder path = PathPatternRequestMatcher.withDefaults();

        http
            .csrf(csrf -> csrf.disable())                      // sin cookies de sesión
            .headers(h -> h.frameOptions(frame -> frame.sameOrigin()))   // consola H2
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth                // de arriba hacia abajo
                .requestMatchers(toH2Console()).permitAll()
                .requestMatchers(path.matcher("/error")).permitAll()
                .requestMatchers(path.matcher(HttpMethod.POST, "/api/auth/**")).permitAll()
                .requestMatchers(path.matcher(HttpMethod.GET, "/api/productos/**"))
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers(path.matcher("/api/productos/**")).hasRole("ADMIN")
                .requestMatchers(path.matcher("/api/**")).hasAnyRole("USER", "ADMIN")
                .anyRequest().denyAll()
            )
            // Sin token válido -> 401 (sin esto Spring respondería 403)
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(new JwtAuthFilter(jwtService),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
