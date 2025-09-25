package gestionlibreria.util;

import gestionlibreria.model.Venta;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera reportes simples en archivos de texto.
 */
public final class GeneradorReporte {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private GeneradorReporte() {
    }

    /**
     * Genera un reporte de ventas en un archivo txt.
     */
    public static void generarReporteVentas(String ruta, List<Venta> ventas) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add("REPORTE DE VENTAS");
        lineas.add("=================");
        for (Venta venta : ventas) {
            lineas.add("Venta: " + venta.getIdVenta());
            lineas.add("Fecha: " + venta.getFecha().format(FORMATO_FECHA));
            lineas.add("Cliente: " + venta.getCliente().getNombreCliente());
            lineas.add("Total: $" + venta.calcularTotal());
            lineas.add("Items:");
            venta.getItems().forEach(item -> lineas.add(" - " + item.getLibro().getTitulo() +
                    " x" + item.getCantidad() + " = $" + item.calcularSubtotal()));
            lineas.add("");
        }
        ArchivoUtil.escribirLineas(ruta, lineas);
    }
}
