package pe.edu.tecsup.lab09.dto;

import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        CategoriaProducto categoria,
        BigDecimal precio
) {
    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(),
                p.getCategoria(), p.getPrecio());
    }
}
