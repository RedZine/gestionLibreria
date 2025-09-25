package gestionlibreria.util;

/**
 * Excepción para indicar que no se encontró una entidad solicitada.
 */
public class EntidadNoEncontradaException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje descriptivo del faltante.
     *
     * @param mensaje detalle de la entidad no encontrada
     */
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con el mensaje y la causa original.
     *
     * @param mensaje detalle de la entidad no encontrada
     * @param causa causa que originó el error
     */
    public EntidadNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
