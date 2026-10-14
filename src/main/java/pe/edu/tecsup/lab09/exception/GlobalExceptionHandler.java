package pe.edu.tecsup.lab09.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.tecsup.lab09.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex,
                                                  HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex,
                                                  HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> credenciales(CredencialesInvalidasException ex,
                                                      HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccesoDenegadoException ex,
                                                        HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req, null);
    }

    /** Falla un @Valid: devuelve el mensaje de cada campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex,
                                                    HttpServletRequest req) {
        Map<String, String> detalles = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> detalles.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Datos inválidos", req, detalles);
    }

    /** JSON mal escrito o categoría que no existe en el enum. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> unreadable(HttpMessageNotReadableException ex,
                                                    HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST,
                "JSON mal formado o valor no permitido (revise la categoría)", req, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String mensaje,
                                                HttpServletRequest req,
                                                Map<String, String> detalles) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensaje, req.getRequestURI(), detalles);
        return ResponseEntity.status(status).body(body);
    }
}
