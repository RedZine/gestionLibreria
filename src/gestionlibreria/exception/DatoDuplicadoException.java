package gestionlibreria.exception;

/**
 * Se lanza cuando se intenta registrar un elemento que ya existe
 * dentro de una colección (por ejemplo ISBN repetidos).
 */
public class DatoDuplicadoException extends Exception {
    public DatoDuplicadoException(String message) {
        super(message);
    }
}
