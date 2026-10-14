package pe.edu.tecsup.lab09.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.tecsup.lab09.dto.ProductoRequest;
import pe.edu.tecsup.lab09.dto.ProductoResponse;
import pe.edu.tecsup.lab09.exception.ResourceNotFoundException;
import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;
import pe.edu.tecsup.lab09.repository.ProductoRepository;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Endpoint 4: POST /api/productos */
    public ProductoResponse crear(ProductoRequest req) {
        Producto p = new Producto();
        p.setNombre(req.nombre().trim());
        p.setDescripcion(req.descripcion());
        p.setCategoria(req.categoria());
        p.setPrecio(req.precio());
        return ProductoResponse.from(productoRepository.save(p));
    }

    /** Endpoint 5: GET /api/productos?categoria= (filtro opcional) */
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(CategoriaProducto categoria) {
        List<Producto> productos = (categoria == null)
                ? productoRepository.findAll()
                : productoRepository.findByCategoriaOrderByNombre(categoria);
        return productos.stream().map(ProductoResponse::from).toList();
    }

    /** Endpoint 6: GET /api/productos/{id} */
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscarEntidad(id));
    }

    /** Usado por CalificacionService para validar la FK. */
    @Transactional(readOnly = true)
    public Producto buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
