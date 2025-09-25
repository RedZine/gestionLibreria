package gestionlibreria.ui.console;

import gestionlibreria.config.AppConfig;
import gestionlibreria.repository.CatalogoRepository;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.PromocionRepository;
import gestionlibreria.repository.VentaRepository;
import gestionlibreria.util.GeneradorReporte;
import gestionlibreria.util.EntradaConsolaUtil;
import java.io.IOException;
import java.util.Scanner;

/**
 * Controla el menú principal de la aplicación en consola.
 */
public class MenuPrincipal {
    private final CatalogoRepository catalogoRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final PromocionRepository promocionRepository;
    private final Scanner scanner;
    private final MenuLibros menuLibros;
    private final MenuVentas menuVentas;

    public MenuPrincipal(CatalogoRepository catalogoRepository, ClienteRepository clienteRepository,
                         VentaRepository ventaRepository, PromocionRepository promocionRepository, Scanner scanner) {
        this.catalogoRepository = catalogoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.promocionRepository = promocionRepository;
        this.scanner = scanner;
        this.menuLibros = new MenuLibros(catalogoRepository, scanner);
        this.menuVentas = new MenuVentas(ventaRepository, catalogoRepository, clienteRepository, scanner);
    }

    /**
     * Ejecuta el ciclo principal de opciones del sistema.
     */
    public void mostrar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n=== Sistema de Gestión de Librería ===");
            System.out.println("1. Gestión de libros");
            System.out.println("2. Gestión de ventas");
            System.out.println("3. Generar reporte de ventas");
            System.out.println("0. Salir");
            String opcion = EntradaConsolaUtil.leerTexto(scanner, "Seleccione opción: ");
            switch (opcion) {
                case "1":
                    menuLibros.mostrar();
                    break;
                case "2":
                    menuVentas.mostrar();
                    break;
                case "3":
                    generarReporteVentas();
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void generarReporteVentas() {
        try {
            GeneradorReporte.generarReporteVentas(AppConfig.RUTA_REPORTE_VENTAS, ventaRepository.obtenerTodas());
            System.out.println("Reporte generado en: " + AppConfig.RUTA_REPORTE_VENTAS);
        } catch (IOException ex) {
            System.out.println("Error al generar el reporte: " + ex.getMessage());
        }
    }
}
