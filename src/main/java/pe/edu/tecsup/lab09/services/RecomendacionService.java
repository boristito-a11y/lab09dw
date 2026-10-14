package pe.edu.tecsup.lab09.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.tecsup.lab09.dto.RecomendacionResponse;
import pe.edu.tecsup.lab09.models.Calificacion;
import pe.edu.tecsup.lab09.models.CategoriaProducto;
import pe.edu.tecsup.lab09.models.Producto;
import pe.edu.tecsup.lab09.models.Usuario;
import pe.edu.tecsup.lab09.repository.CalificacionRepository;
import pe.edu.tecsup.lab09.repository.ProductoRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Endpoint 10: GET /api/recomendaciones/me
 *
 * Filtrado colaborativo simple basado en usuarios:
 *  1. "Me gusta" = productos que el usuario del token calificó con 4 o 5.
 *  2. Usuarios similares = otros usuarios que también calificaron con 4 o 5 alguno
 *     de esos productos.
 *  3. Candidatos = productos que el usuario aún NO ha calificado.
 *  4. Puntaje de cada candidato:
 *       base  = promedio de los usuarios similares (si lo calificaron),
 *               si no, promedio general del producto, si no, 0
 *       bonus = +1 si es de una categoría que al usuario le gusta
 *  5. Se ordena por puntaje (desc) y se devuelven los "limite" primeros.
 * Si el usuario no calificó nada, el resultado son los productos mejor calificados
 * (recomendación por popularidad).
 */
@Service
@Transactional(readOnly = true)
public class RecomendacionService {

    private static final int PUNTUACION_ME_GUSTA = 4;
    private static final double BONUS_CATEGORIA = 1.0;
    private static final int LIMITE_MAXIMO = 20;

    private final CalificacionRepository calificacionRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    public RecomendacionService(CalificacionRepository calificacionRepository,
                                ProductoRepository productoRepository,
                                UsuarioService usuarioService) {
        this.calificacionRepository = calificacionRepository;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    public List<RecomendacionResponse> recomendar(String username, int limite) {
        int n = Math.max(1, Math.min(limite, LIMITE_MAXIMO));
        Usuario yo = usuarioService.buscarPorUsername(username);   // username viene del token

        List<Calificacion> todas = calificacionRepository.findAllConUsuarioYProducto();

        // 1. Lo que ya califiqué y lo que me gusta
        List<Calificacion> mias = todas.stream()
                .filter(c -> c.getUsuario().getId().equals(yo.getId()))
                .toList();
        Set<Long> yaCalificados = mias.stream()
                .map(c -> c.getProducto().getId())
                .collect(Collectors.toSet());
        Set<Long> meGustan = mias.stream()
                .filter(c -> c.getPuntuacion() >= PUNTUACION_ME_GUSTA)
                .map(c -> c.getProducto().getId())
                .collect(Collectors.toSet());
        Set<CategoriaProducto> categoriasFavoritas = mias.stream()
                .filter(c -> c.getPuntuacion() >= PUNTUACION_ME_GUSTA)
                .map(c -> c.getProducto().getCategoria())
                .collect(Collectors.toSet());

        // 2. Usuarios con gustos similares
        Set<Long> similares = todas.stream()
                .filter(c -> !c.getUsuario().getId().equals(yo.getId()))
                .filter(c -> c.getPuntuacion() >= PUNTUACION_ME_GUSTA)
                .filter(c -> meGustan.contains(c.getProducto().getId()))
                .map(c -> c.getUsuario().getId())
                .collect(Collectors.toSet());

        // Calificaciones agrupadas por producto
        Map<Long, List<Calificacion>> porProducto = todas.stream()
                .collect(Collectors.groupingBy(c -> c.getProducto().getId()));

        // 3 y 4. Candidatos con su puntaje
        List<RecomendacionResponse> resultado = new ArrayList<>();
        for (Producto p : productoRepository.findAll()) {
            if (yaCalificados.contains(p.getId())) {
                continue;                                        // ya lo conoce
            }
            List<Calificacion> deEste = porProducto.getOrDefault(p.getId(), List.of());
            OptionalDouble promSimilares = deEste.stream()
                    .filter(c -> similares.contains(c.getUsuario().getId()))
                    .mapToInt(Calificacion::getPuntuacion).average();
            OptionalDouble promGeneral = deEste.stream()
                    .mapToInt(Calificacion::getPuntuacion).average();
            boolean categoriaFavorita = categoriasFavoritas.contains(p.getCategoria());

            double base;
            String motivo;
            if (promSimilares.isPresent()) {
                base = promSimilares.getAsDouble();
                motivo = String.format("Usuarios con gustos similares lo calificaron con %.1f",
                        base);
            } else if (promGeneral.isPresent()) {
                base = promGeneral.getAsDouble();
                motivo = String.format("Promedio general de %.1f en %d calificación(es)",
                        base, deEste.size());
            } else {
                base = 0;
                motivo = "Aún no tiene calificaciones: ¡descúbrelo!";
            }
            if (categoriaFavorita) {
                motivo += " · categoría que te gusta (" + p.getCategoria() + ")";
            }
            double puntaje = base + (categoriaFavorita ? BONUS_CATEGORIA : 0);
            resultado.add(RecomendacionResponse.from(p, redondear(puntaje), motivo));
        }

        // 5. Ordenar y limitar
        return resultado.stream()
                .sorted(Comparator.comparingDouble(RecomendacionResponse::puntaje).reversed()
                        .thenComparing(RecomendacionResponse::nombre))
                .limit(n)
                .toList();
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
