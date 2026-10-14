package pe.edu.tecsup.lab09.dto;

import pe.edu.tecsup.lab09.models.Rol;
import pe.edu.tecsup.lab09.models.Usuario;

/** DTO de salida: el hash de la contraseña NUNCA se devuelve. */
public record UsuarioResponse(
        Long id,
        String username,
        String nombre,
        Rol rol
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getNombre(), u.getRol());
    }
}
