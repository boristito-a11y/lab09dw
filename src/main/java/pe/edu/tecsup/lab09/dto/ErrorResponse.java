package pe.edu.tecsup.lab09.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> detalles
) {}
