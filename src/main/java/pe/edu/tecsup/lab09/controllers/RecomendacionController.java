package pe.edu.tecsup.lab09.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.tecsup.lab09.dto.RecomendacionResponse;
import pe.edu.tecsup.lab09.services.RecomendacionService;

import java.util.List;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    /** USER/ADMIN (regla /api/** en SecurityConfig). Ej: GET /api/recomendaciones/me?limite=3 */
    @GetMapping("/me")
    public List<RecomendacionResponse> misRecomendaciones(
            Authentication auth,
            @RequestParam(defaultValue = "5") int limite) {
        return recomendacionService.recomendar(auth.getName(), limite);
    }
}
