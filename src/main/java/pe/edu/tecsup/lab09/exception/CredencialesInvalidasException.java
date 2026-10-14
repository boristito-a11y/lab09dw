package pe.edu.tecsup.lab09.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        // Mismo mensaje si falla el usuario o la clave: no damos pistas al atacante
        super("Usuario o contraseña incorrectos");
    }
}
