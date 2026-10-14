package pe.edu.tecsup.lab09.dto;

import pe.edu.tecsup.lab09.models.Calificacion;

import java.time.LocalDate;

public record CalificacionResponse(
        Long id,
        String usuario,
        Long productoId,
        String producto,
        Integer puntuacion,
        String comentario,
        LocalDate fecha
) {
    public static CalificacionResponse from(Calificacion c) {
        return new CalificacionResponse(c.getId(), c.getUsuario().getUsername(),
                c.getProducto().getId(), c.getProducto().getNombre(),
                c.getPuntuacion(), c.getComentario(), c.getFecha());
    }
}
