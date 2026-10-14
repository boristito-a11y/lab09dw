package pe.edu.tecsup.lab09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.tecsup.lab09.models.Calificacion;

import java.util.List;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    /** Usado por RecomendacionService: trae usuario y producto en una sola consulta (sin N+1). */
    @Query("select c from Calificacion c join fetch c.usuario join fetch c.producto")
    List<Calificacion> findAllConUsuarioYProducto();
}
