package pe.edu.tecsup.lab09.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;
import pe.edu.tecsup.lab09.models.Rol;
import pe.edu.tecsup.lab09.models.Usuario;
import pe.edu.tecsup.lab09.repository.ProductoRepository;
import pe.edu.tecsup.lab09.repository.UsuarioRepository;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador");
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);
        }
        if (productoRepository.count() == 0) {               // ids 1 a 7 en este orden
            crearProducto("Laptop 15 Core i5", CategoriaProducto.TECNOLOGIA, "2499.00");
            crearProducto("Mouse inalámbrico", CategoriaProducto.TECNOLOGIA, "59.90");
            crearProducto("Audífonos Bluetooth", CategoriaProducto.TECNOLOGIA, "229.00");
            crearProducto("Licuadora 600 W", CategoriaProducto.HOGAR, "199.00");
            crearProducto("Zapatillas running", CategoriaProducto.DEPORTES, "349.00");
            crearProducto("Clean Code", CategoriaProducto.LIBROS, "159.00");
            crearProducto("El Principito", CategoriaProducto.LIBROS, "39.90");
        }
    }

    private void crearProducto(String nombre, CategoriaProducto categoria, String precio) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecio(new BigDecimal(precio));
        productoRepository.save(p);
    }
}
