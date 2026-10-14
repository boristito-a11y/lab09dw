package pe.edu.tecsup.lab09.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, Object id) {
        super(recurso + " '" + id + "' no existe");
    }
}
