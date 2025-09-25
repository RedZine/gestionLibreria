package gestionlibreria.app;

import gestionlibreria.repository.CsvLibroRepository;
import gestionlibreria.repository.ArchivoClienteRepository;
import gestionlibreria.repository.ArchivoPromocionRepository;
import gestionlibreria.repository.ArchivoVentaRepository;
import gestionlibreria.repository.CatalogoRepository;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.PromocionRepository;
import gestionlibreria.repository.VentaRepository;
import gestionlibreria.ui.console.MenuPrincipal;
import java.io.IOException;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicación de gestión de librería.
 */
public class LibreriaApp {

    /**
     * Inicia la aplicación y gestiona el ciclo de vida de los datos.
     */
    public static void main(String[] args) {
        CatalogoRepository catalogoRepository = new CsvLibroRepository();
        ClienteRepository clienteRepository = new ArchivoClienteRepository();
        PromocionRepository promocionRepository = new ArchivoPromocionRepository();
        VentaRepository ventaRepository = new ArchivoVentaRepository(catalogoRepository, clienteRepository);
        cargarDatos(catalogoRepository, clienteRepository, promocionRepository, ventaRepository);
        try (Scanner scanner = new Scanner(System.in)) {
            MenuPrincipal menuPrincipal = new MenuPrincipal(catalogoRepository, clienteRepository, ventaRepository, promocionRepository, scanner);
            menuPrincipal.mostrar();
        }
        guardarDatos(catalogoRepository, clienteRepository, promocionRepository, ventaRepository);
    }

    private static void cargarDatos(CatalogoRepository catalogoRepository, ClienteRepository clienteRepository,
            PromocionRepository promocionRepository, VentaRepository ventaRepository) {
        try {
            catalogoRepository.cargar();
            clienteRepository.cargar();
            promocionRepository.cargar();
            ventaRepository.cargar();
        } catch (IOException ex) {
            System.out.println("Error al cargar datos: " + ex.getMessage());
        }
    }

    private static void guardarDatos(CatalogoRepository catalogoRepository, ClienteRepository clienteRepository,
            PromocionRepository promocionRepository, VentaRepository ventaRepository) {
        try {
            catalogoRepository.guardar();
            clienteRepository.guardar();
            promocionRepository.guardar();
            ventaRepository.guardar();
        } catch (IOException ex) {
            System.out.println("Error al guardar datos: " + ex.getMessage());
        }
    }
}
