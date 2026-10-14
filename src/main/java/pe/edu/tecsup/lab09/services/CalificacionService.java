package pe.edu.tecsup.lab09.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.tecsup.lab09.dto.CalificacionRequest;
import pe.edu.tecsup.lab09.dto.CalificacionResponse;
import pe.edu.tecsup.lab09.exception.AccesoDenegadoException;
import pe.edu.tecsup.lab09.exception.BusinessException;
import pe.edu.tecsup.lab09.exception.ResourceNotFoundException;
import pe.edu.tecsup.lab09.models.Calificacion;
import pe.edu.tecsup.lab09.models.Producto;
import pe.edu.tecsup.lab09.models.Usuario;
import pe.edu.tecsup.lab09.repository.CalificacionRepository;

import java.util.List;

@Service
@Transactional
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public CalificacionService(CalificacionRepository calificacionRepository,
                               UsuarioService usuarioService,
                               ProductoService productoService) {
        this.calificacionRepository = calificacionRepository;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    /** Endpoint 7: POST /api/calificaciones. El username viene del token, NO del body. */
    public CalificacionResponse crear(String username, CalificacionRequest req) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        Producto producto = productoService.buscarEntidad(req.productoId()); // 404

        boolean yaCalifico = calificacionRepository
                .existsByUsuarioIdAndProductoId(usuario.getId(), producto.getId());
        if (yaCalifico) {
            throw new BusinessException("Ya calificaste el producto " + producto.getNombre());
        }
        Calificacion c = new Calificacion();
        c.setUsuario(usuario);
        c.setProducto(producto);
        c.setPuntuacion(req.puntuacion());
        c.setComentario(req.comentario());
        return CalificacionResponse.from(calificacionRepository.save(c));
    }

    /** Endpoint 8: GET /api/calificaciones/mias */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> listarMias(String username) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return calificacionRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId())
                .stream().map(CalificacionResponse::from).toList();
    }

    /** Endpoint 9: DELETE /api/calificaciones/{id}. Solo el dueño o un ADMIN. */
    public void eliminar(Long id, String username, boolean esAdmin) {
        Calificacion c = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación", id));
        boolean esDueno = c.getUsuario().getUsername().equals(username);
        if (!esDueno && !esAdmin) {
            throw new AccesoDenegadoException("Solo puedes eliminar tus calificaciones");
        }
        calificacionRepository.delete(c);
    }
}
