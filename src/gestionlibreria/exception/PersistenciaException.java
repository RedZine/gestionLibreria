package gestionlibreria.exception;

/**
 * Se utiliza para encapsular errores de Entrada/Salida asociados
 * a la carga o guardado de los datos en disco.
 */
public class PersistenciaException extends Exception {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenciaException(String message) {
        super(message);
    }
}
