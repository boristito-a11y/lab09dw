package pe.edu.tecsup.lab09.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.tecsup.lab09.dto.ProductoRequest;
import pe.edu.tecsup.lab09.dto.ProductoResponse;
import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.services.ProductoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** Solo ADMIN (regla en SecurityConfig). */
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest req) {
        ProductoResponse creado = productoService.crear(req);
        return ResponseEntity.created(URI.create("/api/productos/" + creado.id()))
                .body(creado);
    }

    /** Ej: GET /api/productos?categoria=LIBROS */
    @GetMapping
    public List<ProductoResponse> listar(
            @RequestParam(required = false) CategoriaProducto categoria) {
        return productoService.listar(categoria);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }
}
