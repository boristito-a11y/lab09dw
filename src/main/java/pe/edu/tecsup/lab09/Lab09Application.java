package pe.edu.tecsup.lab09;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Se excluye UserDetailsServiceAutoConfiguration porque los usuarios viven en la tabla
 * "usuarios": sin esta exclusión Spring Boot crearía un usuario "user" con una clave
 * aleatoria impresa en la consola, que en esta API no sirve.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class Lab09Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab09Application.class, args);
    }
}
