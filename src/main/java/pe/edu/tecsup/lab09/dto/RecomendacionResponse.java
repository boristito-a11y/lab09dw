package pe.edu.tecsup.lab09.dto;

import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;

import java.math.BigDecimal;

/** Un producto recomendado, con el puntaje calculado y el motivo de la recomendación. */
public record RecomendacionResponse(
        Long productoId,
        String nombre,
        CategoriaProducto categoria,
        BigDecimal precio,
        double puntaje,
        String motivo
) {
    public static RecomendacionResponse from(Producto p, double puntaje, String motivo) {
        return new RecomendacionResponse(p.getId(), p.getNombre(), p.getCategoria(),
                p.getPrecio(), puntaje, motivo);
    }
}
