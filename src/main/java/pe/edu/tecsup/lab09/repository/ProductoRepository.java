package pe.edu.tecsup.lab09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaOrderByNombre(CategoriaProducto categoria);
}
