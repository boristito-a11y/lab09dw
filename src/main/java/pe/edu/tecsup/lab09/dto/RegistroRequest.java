package pe.edu.tecsup.lab09.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Sin campo rol: todo usuario registrado es USER (nadie se registra como ADMIN). */
public record RegistroRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50, message = "Entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-z0-9._]+$", message = "Solo minúsculas, números, . y _")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "Entre 6 y 72 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        String nombre
) {}
