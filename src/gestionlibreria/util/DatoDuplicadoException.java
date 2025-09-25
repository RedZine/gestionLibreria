package gestionlibreria.util;

/**
 * Excepción utilizada cuando se intenta registrar un dato ya existente.
 */
public class DatoDuplicadoException extends Exception {
    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
