package gestionlibreria.util;

/**
 * Excepción para indicar que un dato requerido no existe.
 */
public class DatoNoEncontradoException extends Exception {
    public DatoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
