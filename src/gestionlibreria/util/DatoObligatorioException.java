package gestionlibreria.util;

/**
 * Excepción para indicar que falta un dato obligatorio o es inválido.
 */
public class DatoObligatorioException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje detallado del problema.
     *
     * @param mensaje descripción del dato faltante o inválido
     */
    public DatoObligatorioException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con el mensaje y la causa original.
     *
     * @param mensaje descripción del dato faltante o inválido
     * @param causa causa que originó el error
     */
    public DatoObligatorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
