# Laboratorio 09 – API REST con JWT (Spring Boot)

API de una tienda en línea: los usuarios inician sesión con JWT, califican productos del 1 al 5
y reciben recomendaciones. El ADMIN gestiona el catálogo.

- Spring Boot 3.5.6 · Java 17+ · Spring Security · JJWT 0.12.6 · Spring Data JPA · H2
- Grupo `pe.edu.tecsup`, artefacto `lab09`

---

## Paso a paso del laboratorio

### Paso 1 – Crear el proyecto (sección 3)
1. En https://start.spring.io elige **Maven**, **Java 17**, **Spring Boot 3.5.x**, group `pe.edu.tecsup`, artifact `lab09`.
2. Dependencias: Spring Web, Spring Data JPA, H2 Database, Validation, Spring Security.
3. Agrega las 3 dependencias de JJWT al `pom.xml` (ya están en este repo).
4. Copia el `application.properties` (en `src/main/resources`).

### Paso 2 – Escribir las clases en orden (sección 5)
| Orden | Paquete | Clases |
|---|---|---|
| 1 | `exception/`, `dto/` | `ResourceNotFoundException`, `BusinessException`, `CredencialesInvalidasException`, `AccesoDenegadoException`, `ErrorResponse`, `GlobalExceptionHandler` |
| 2 | `models/` | `Rol`, `Usuario` |
| 3 | `dto/` | `RegistroRequest`, `LoginRequest`, `TokenResponse`, `UsuarioResponse` |
| 4 | `repository/` | `UsuarioRepository` |
| 5 | `security/`, `config/` | `JwtService`, `JwtAuthFilter`, `SecurityConfig` |
| 6 | `services/`, `controllers/` | `AuthService`, `UsuarioService`, `AuthController`, `UsuarioController` |
| 7 | Producto | `CategoriaProducto`, `Producto`, `ProductoRequest`, `ProductoResponse`, `ProductoRepository`, `ProductoService`, `ProductoController` |
| 8 | Calificación | `Calificacion`, `CalificacionRequest`, `CalificacionResponse`, `CalificacionRepository`, `CalificacionService`, `CalificacionController` |
| 9 | Seeder | `DataInitializer` (admin/admin123 + 7 productos) |
| 10 | **Recomendación (tu parte)** | `RecomendacionResponse`, `RecomendacionService`, `RecomendacionController` |

### Paso 3 – Ejecutar
```bash
mvn spring-boot:run
```
(o Run sobre `Lab09Application` en IntelliJ).

### Paso 4 – Checkpoint en la consola H2
1. Abre http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:lab09db`, usuario `sa`, sin contraseña → **Connect**.
3. Deben aparecer **USUARIOS**, **PRODUCTOS** y **CALIFICACIONES**.
4. Ejecuta y captura:
```sql
SELECT * FROM USUARIOS;          -- admin con password en hash BCrypt ($2a$10$...)
SELECT * FROM PRODUCTOS;         -- 7 productos del seeder
SELECT * FROM CALIFICACIONES;

-- Las 2 FKs y la restricción única creadas por Hibernate
SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_NAME = 'CALIFICACIONES';
```
Resultado esperado: 1 `PRIMARY KEY`, 2 `FOREIGN KEY` (`usuario_id → usuarios`, `producto_id → productos`) y 1 `UNIQUE` (`usuario_id, producto_id`).

### Paso 5 – Probar en Postman
1. Postman → **Import** → `postman/Lab09.postman_collection.json`.
2. Ejecuta las peticiones **en orden** (carpetas 1 → 5). Los login guardan automáticamente
   `{{token}}` (ana), `{{tokenLuis}}` y `{{tokenAdmin}}` con el script *Post-response*:
   ```js
   pm.collectionVariables.set("token", pm.response.json().token);
   ```
3. Toma captura de cada petición mostrando **URL, body, Authorization y código de respuesta**.

> Si reinicias la app, la BD en memoria se borra: vuelve a ejecutar desde el registro.

---

## Endpoints

| # | Método | Ruta | Acceso | Respuesta |
|---|---|---|---|---|
| 1 | POST | `/api/auth/registro` | Público | 201 · 400 · 409 |
| 2 | POST | `/api/auth/login` | Público | 200 · 401 |
| 3 | GET | `/api/usuarios/me` | USER/ADMIN | 200 · 401 |
| 4 | POST | `/api/productos` | ADMIN | 201 · 400 · 403 |
| 5 | GET | `/api/productos?categoria=` | USER/ADMIN | 200 |
| 6 | GET | `/api/productos/{id}` | USER/ADMIN | 200 · 404 |
| 7 | POST | `/api/calificaciones` | USER/ADMIN | 201 · 400 · 404 · 409 |
| 8 | GET | `/api/calificaciones/mias` | USER/ADMIN | 200 |
| 9 | DELETE | `/api/calificaciones/{id}` | Dueño o ADMIN | 204 · 403 · 404 |
| 10 | GET | `/api/recomendaciones/me?limite=5` | USER/ADMIN | 200 · 401 |

---

## Sección 8 – Endpoint de recomendación (implementación propia)

`GET /api/recomendaciones/me?limite=5` — el usuario se toma del token (`auth.getName()`), nunca de un parámetro.

Algoritmo (**filtrado colaborativo basado en usuarios**), en `RecomendacionService`:
1. **Me gusta**: productos que el usuario calificó con 4 o 5 → también se obtienen sus **categorías favoritas**.
2. **Usuarios similares**: otros usuarios que calificaron con 4 o 5 alguno de esos mismos productos.
3. **Candidatos**: productos que el usuario **todavía no calificó** (no se recomienda lo que ya conoce).
4. **Puntaje** = promedio que le dieron los usuarios similares (si no hay, el promedio general; si no tiene calificaciones, 0) **+1** si es de una categoría favorita.
5. Se ordena por puntaje descendente y se devuelven los `limite` primeros (1 a 20).

Si el usuario aún no calificó nada (*cold start*), el resultado equivale a los productos mejor calificados (popularidad).
Reutiliza `UsuarioService.buscarPorUsername`, `CalificacionRepository` (se agregó una consulta con `join fetch` para evitar N+1) y `ProductoRepository`.

Ejemplo – ana calificó Audífonos (5) y Clean Code (4); luis calificó Audífonos (5), Laptop (5), El Principito (4) y Licuadora (2):
```json
[
  { "productoId": 1, "nombre": "Laptop 15 Core i5", "categoria": "TECNOLOGIA", "precio": 2499.00, "puntaje": 6.0,
    "motivo": "Usuarios con gustos similares lo calificaron con 5.0 · categoría que te gusta (TECNOLOGIA)" },
  { "productoId": 7, "nombre": "El Principito", "categoria": "LIBROS", "precio": 39.90, "puntaje": 5.0,
    "motivo": "Usuarios con gustos similares lo calificaron con 4.0 · categoría que te gusta (LIBROS)" },
  { "productoId": 4, "nombre": "Licuadora 600 W", "categoria": "HOGAR", "precio": 199.00, "puntaje": 2.0,
    "motivo": "Usuarios con gustos similares lo calificaron con 2.0" }
]
```

---

## Evidencias a capturar (sección 9)

**Postman**
- [ ] Registro 201, registro duplicado 409, registro inválido 400 (con `detalles`)
- [ ] Login 200 (token) y login incorrecto 401
- [ ] `/usuarios/me` con token 200 y sin token 401
- [ ] Crear producto con ADMIN 201, con USER 403, categoría inválida 400
- [ ] Listar productos, filtrar por categoría, producto 99 → 404
- [ ] Crear calificaciones 201, duplicada 409, puntuación 9 → 400
- [ ] `/calificaciones/mias` 200
- [ ] `/recomendaciones/me` de ana y de admin 200, sin token 401
- [ ] DELETE de otro usuario 403, propio 204, ADMIN 204
- [ ] (opcional) pegar el token en https://jwt.io para mostrar `sub`, `rol`, `iat`, `exp`

**H2**
- [ ] Las 3 tablas creadas
- [ ] `USUARIOS` con contraseñas en hash BCrypt
- [ ] Restricciones de `CALIFICACIONES` (2 FK + UNIQUE)

---

## Vulnerabilidades identificadas en la API

| # | Vulnerabilidad | Riesgo | Mitigación |
|---|---|---|---|
| 1 | Clave secreta JWT escrita en `application.properties` y subida al repositorio | Quien la lea puede firmar tokens falsos, incluso con `rol: ADMIN` | Leerla de una variable de entorno / gestor de secretos (`app.jwt.secret=${JWT_SECRET}`) |
| 2 | Credenciales del admin fijas (`admin/admin123`) en `DataInitializer` | Acceso total al catálogo con una clave conocida | Clave inicial por variable de entorno y cambio obligatorio |
| 3 | Consola H2 habilitada y `frameOptions sameOrigin` | Acceso directo a la BD si se despliega así | Desactivar `spring.h2.console.enabled` en producción |
| 4 | Sin límite de intentos en `/api/auth/login` | Fuerza bruta de contraseñas | Rate limiting / bloqueo temporal tras N intentos |
| 5 | Token sin revocación (stateless): no hay logout | Un token robado sirve hasta que expira (60 min) | Expiración corta + refresh token, o lista negra de tokens |
| 6 | El rol se lee del token, no de la BD | Si se cambia el rol de un usuario, su token antiguo mantiene el rol anterior hasta expirar | Consultar el rol en BD o tokens de corta duración |
| 7 | Comunicación por HTTP | El token puede ser interceptado (man-in-the-middle) | Usar HTTPS obligatorio |
| 8 | `/api/auth/registro` sin captcha ni límite | Creación masiva de cuentas / spam de calificaciones | Captcha, límite por IP, verificación de correo |
| 9 | Contraseña mínima de 6 caracteres sin complejidad | Contraseñas débiles | Política más estricta (longitud ≥ 8, mayúsculas, números) |
| 10 | `spring.jpa.show-sql=true` | Expone consultas y datos en los logs | Desactivarlo en producción |
| 11 | `GET /api/productos` sin paginación | Respuestas enormes / consumo de recursos (DoS) | Paginación con `Pageable` |

**Buenas prácticas que la API sí aplica:** contraseñas con BCrypt, el hash nunca se devuelve (DTO `UsuarioResponse`), el rol nunca viene del cliente en el registro, el usuario de una calificación se toma del token (evita suplantación / IDOR), mismo mensaje para usuario o clave incorrectos (no revela usuarios), validación con `@Valid`, sesión `STATELESS`, `denyAll()` por defecto.

---

## B. Tabla de funcionalidades de toda la API

| Paso | Descripción |
|---|---|
| 1 | **Registro de usuario** (`POST /api/auth/registro`): crea una cuenta con rol USER; valida username (3–50, minúsculas), contraseña (6–72) y nombre; guarda la contraseña con BCrypt. 201 / 400 / 409 si el username existe. |
| 2 | **Inicio de sesión** (`POST /api/auth/login`): verifica la contraseña con BCrypt y devuelve un JWT firmado HS256 con `sub`, `rol`, `iat` y `exp` (60 min). 401 si las credenciales son incorrectas. |
| 3 | **Filtro JWT** (`JwtAuthFilter`): en cada petición lee `Authorization: Bearer <token>`, valida firma y expiración y registra al usuario y su rol en el `SecurityContext`. Token ausente o inválido → 401. |
| 4 | **Autorización por roles** (`SecurityConfig`): rutas públicas (`/api/auth/**`, H2), GET de productos para USER/ADMIN, escritura de productos solo ADMIN (403 a USER), resto de `/api/**` autenticado, todo lo demás `denyAll`. |
| 5 | **Mi perfil** (`GET /api/usuarios/me`): devuelve id, username, nombre y rol del dueño del token, sin la contraseña. |
| 6 | **Crear producto** (`POST /api/productos`, ADMIN): valida nombre, descripción, categoría del enum y precio > 0 con 2 decimales. 201 con cabecera `Location`. |
| 7 | **Listar productos** (`GET /api/productos?categoria=`): lista todo el catálogo o filtra por TECNOLOGIA, HOGAR, DEPORTES o LIBROS ordenado por nombre. |
| 8 | **Obtener producto** (`GET /api/productos/{id}`): devuelve un producto o 404 si no existe. |
| 9 | **Calificar producto** (`POST /api/calificaciones`): el usuario sale del token; valida puntuación 1–5 y comentario ≤ 300; asigna la fecha con `@PrePersist`; 404 si el producto no existe y 409 si ya lo calificó. |
| 10 | **Mis calificaciones** (`GET /api/calificaciones/mias`): lista las calificaciones del usuario del token, de la más reciente a la más antigua. |
| 11 | **Eliminar calificación** (`DELETE /api/calificaciones/{id}`): solo el dueño o un ADMIN; 204, 403 si es de otro usuario, 404 si no existe. |
| 12 | **Recomendaciones** (`GET /api/recomendaciones/me?limite=`): sugiere productos no calificados según usuarios con gustos similares y categorías favoritas, con puntaje y motivo; si no hay historial, por popularidad. |
| 13 | **Manejo global de errores** (`GlobalExceptionHandler`): respuestas JSON uniformes (`timestamp`, `status`, `error`, `mensaje`, `ruta`, `detalles`) para 400, 401, 403, 404 y 409. |
| 14 | **Persistencia con JPA** : Hibernate crea `usuarios`, `productos` y `calificaciones`, las 2 FKs y la restricción única `(usuario_id, producto_id)` sin escribir SQL. |
| 15 | **Datos iniciales** (`DataInitializer`): crea el usuario admin y 7 productos de prueba al arrancar. |

---

## Conclusiones

1. JWT permite una API **stateless**: el servidor no guarda sesiones; cada petición trae un token firmado que se valida con la clave secreta, por lo que alterar el payload (por ejemplo el rol) invalida la firma y la API responde 401.
2. La **identidad del usuario debe salir del token** (`auth.getName()`) y no del body; de lo contrario cualquiera podría calificar o borrar en nombre de otro (suplantación / IDOR).
3. Spring Security separa **autenticación** (401: no sé quién eres) de **autorización** (403: sé quién eres pero no tienes permiso); las reglas por rol en `SecurityConfig` se evalúan de arriba hacia abajo, por eso el orden importa.
4. Con `@Entity`, `@ManyToOne`, `@JoinColumn` y `@UniqueConstraint`, Hibernate genera las tablas, las claves foráneas y la restricción única sin escribir SQL, y la regla de "una calificación por producto" queda protegida tanto en el servicio (409) como en la BD.
5. Guardar las contraseñas con **BCrypt** y responder siempre con **DTOs** evita exponer datos sensibles como el hash de la contraseña.
6. Centralizar los errores en un `@RestControllerAdvice` mantiene los servicios independientes de HTTP y da al cliente respuestas consistentes.
7. Un sistema de recomendación sencillo puede construirse solo con las calificaciones existentes (filtrado colaborativo), reutilizando repositorios y servicios ya creados.
8. Aunque la API es funcional, tiene vulnerabilidades propias de un entorno de laboratorio (secreto en el repositorio, admin con clave fija, consola H2, sin rate limiting ni HTTPS) que deben corregirse antes de llevarla a producción.
