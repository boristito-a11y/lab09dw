package pe.edu.tecsup.lab09.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.tecsup.lab09.dto.UsuarioResponse;
import pe.edu.tecsup.lab09.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Spring inyecta el Authentication que creó JwtAuthFilter; getName() = claim "sub". */
    @GetMapping("/me")
    public UsuarioResponse perfil(Authentication auth) {
        return usuarioService.perfil(auth.getName());
    }
}
