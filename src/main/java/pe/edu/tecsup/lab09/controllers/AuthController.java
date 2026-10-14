package pe.edu.tecsup.lab09.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.tecsup.lab09.dto.LoginRequest;
import pe.edu.tecsup.lab09.dto.RegistroRequest;
import pe.edu.tecsup.lab09.dto.TokenResponse;
import pe.edu.tecsup.lab09.dto.UsuarioResponse;
import pe.edu.tecsup.lab09.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
