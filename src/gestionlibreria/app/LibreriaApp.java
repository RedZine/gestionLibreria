package gestionlibreria.app;

import gestionlibreria.repository.CatalogoRepository;
import gestionlibreria.repository.CsvLibroRepository;
import gestionlibreria.ui.console.MenuLibros;
import java.io.IOException;
import java.util.Scanner;

/**
 * Punto de entrada del sistema de gestión de librería.
 */
public class LibreriaApp {

    /**
     * Inicia la aplicación cargando el catálogo y mostrando el menú de libros.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        CsvLibroRepository csvRepository = new CsvLibroRepository();
        try {
            csvRepository.cargar();
        } catch (IOException ex) {
            System.out.println("Error al cargar datos: " + ex.getMessage());
        }
        CatalogoRepository catalogoRepository = csvRepository;
        try (Scanner scanner = new Scanner(System.in)) {
            MenuLibros menuLibros = new MenuLibros(catalogoRepository, scanner);
            menuLibros.mostrar();
        }
        try {
            csvRepository.guardar();
        } catch (IOException ex) {
            System.out.println("Error al guardar datos: " + ex.getMessage());
        }
    }
}
