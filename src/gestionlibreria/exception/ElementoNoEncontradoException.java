package gestionlibreria.exception;

/**
 * Excepción checked para indicar que no se encontró el elemento
 * solicitado en la colección o repositorio.
 */
public class ElementoNoEncontradoException extends Exception {
    public ElementoNoEncontradoException(String message) {
        super(message);
    }
}
