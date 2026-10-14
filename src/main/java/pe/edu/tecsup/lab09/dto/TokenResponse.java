package pe.edu.tecsup.lab09.dto;

import pe.edu.tecsup.lab09.models.Rol;

/** Lo que recibe el cliente tras un login correcto. */
public record TokenResponse(
        String token,
        String tipo,                 // siempre "Bearer"
        long expiraEnSegundos,
        String username,
        Rol rol
) {}
