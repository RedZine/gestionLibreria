package gestionlibreria.config;

/**
 * Configuración centralizada de rutas y archivos utilizados por la aplicación.
 */
public final class AppConfig {
    public static final String RUTA_LIBROS = "data/libros.csv";
    public static final String RUTA_CLIENTES = "data/clientes.csv";
    public static final String RUTA_VENTAS = "data/ventas.csv";
    public static final String RUTA_PROMOCIONES = "data/promociones.csv";
    public static final String RUTA_REPORTE_VENTAS = "reportes/reporte_ventas.txt";

    private AppConfig() {
    }
}
