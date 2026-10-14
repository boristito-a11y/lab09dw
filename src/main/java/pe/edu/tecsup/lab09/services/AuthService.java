package pe.edu.tecsup.lab09.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.tecsup.lab09.dto.LoginRequest;
import pe.edu.tecsup.lab09.dto.RegistroRequest;
import pe.edu.tecsup.lab09.dto.TokenResponse;
import pe.edu.tecsup.lab09.dto.UsuarioResponse;
import pe.edu.tecsup.lab09.exception.BusinessException;
import pe.edu.tecsup.lab09.exception.CredencialesInvalidasException;
import pe.edu.tecsup.lab09.models.Rol;
import pe.edu.tecsup.lab09.models.Usuario;
import pe.edu.tecsup.lab09.repository.UsuarioRepository;
import pe.edu.tecsup.lab09.security.JwtService;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Endpoint 1: POST /api/auth/registro */
    public UsuarioResponse registrar(RegistroRequest req) {
        if (usuarioRepository.existsByUsername(req.username())) {
            throw new BusinessException("El username " + req.username() + " ya está en uso");
        }
        Usuario u = new Usuario();
        u.setUsername(req.username());
        u.setPassword(passwordEncoder.encode(req.password()));   // se guarda el hash
        u.setNombre(req.nombre().trim());
        u.setRol(Rol.USER);                                       // nunca viene del cliente
        return UsuarioResponse.from(usuarioRepository.save(u));
    }

    /** Endpoint 2: POST /api/auth/login */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByUsername(req.username().trim().toLowerCase())
                .orElseThrow(CredencialesInvalidasException::new);

        // matches() aplica BCrypt a la clave recibida y la compara con el hash guardado
        if (!passwordEncoder.matches(req.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        String token = jwtService.generarToken(usuario);
        return new TokenResponse(token, "Bearer", jwtService.getExpiracionSegundos(),
                usuario.getUsername(), usuario.getRol());
    }
}
