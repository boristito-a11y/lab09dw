package pe.edu.tecsup.lab09.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
