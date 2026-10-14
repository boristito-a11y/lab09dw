package pe.edu.tecsup.lab09.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.tecsup.lab09.dto.CalificacionRequest;
import pe.edu.tecsup.lab09.dto.CalificacionResponse;
import pe.edu.tecsup.lab09.services.CalificacionService;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    public ResponseEntity<CalificacionResponse> crear(
            @Valid @RequestBody CalificacionRequest req, Authentication auth) {
        // auth.getName() devuelve el claim "sub" del token: el usuario autenticado
        CalificacionResponse creada = calificacionService.crear(auth.getName(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/mias")
    public List<CalificacionResponse> mias(Authentication auth) {
        return calificacionService.listarMias(auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        calificacionService.eliminar(id, auth.getName(), esAdmin);
        return ResponseEntity.noContent().build();
    }
}
