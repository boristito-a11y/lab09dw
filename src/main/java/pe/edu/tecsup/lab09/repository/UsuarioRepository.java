package pe.edu.tecsup.lab09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.tecsup.lab09.models.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
