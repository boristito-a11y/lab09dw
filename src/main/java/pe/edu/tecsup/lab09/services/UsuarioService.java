package pe.edu.tecsup.lab09.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.tecsup.lab09.dto.UsuarioResponse;
import pe.edu.tecsup.lab09.exception.ResourceNotFoundException;
import pe.edu.tecsup.lab09.models.Usuario;
import pe.edu.tecsup.lab09.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Endpoint 3: GET /api/usuarios/me */
    public UsuarioResponse perfil(String username) {
        return UsuarioResponse.from(buscarPorUsername(username));
    }

    /** Usado por los otros servicios: el username viene del token. */
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));
    }
}
