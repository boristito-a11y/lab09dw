package pe.edu.tecsup.lab09.dto;

import jakarta.validation.constraints.*;
import pe.edu.tecsup.lab09.models.CategoriaProducto;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        String nombre,

        @Size(max = 255, message = "Máximo 255 caracteres")
        String descripcion,

        @NotNull(message = "La categoría es obligatoria (TECNOLOGIA, HOGAR, DEPORTES, LIBROS)")
        CategoriaProducto categoria,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que 0")
        @Digits(integer = 8, fraction = 2, message = "Máximo 8 enteros y 2 decimales")
        BigDecimal precio
) {}
