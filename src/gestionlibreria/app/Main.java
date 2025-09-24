// src/gestionlibreria/app/Main.java
package gestionlibreria.app;

import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.repository.CategoriaLibroRepository;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.CsvCategoriaRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.ui.console.MenuPrincipal;
import gestionlibreria.ui.swing.MainWindow;
import gestionlibreria.util.DataSeeder;

import javax.swing.SwingUtilities;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        CategoriaRepository categoriaRepository = new CsvCategoriaRepository();
        LibroRepository libroRepository = new CategoriaLibroRepository(categoriaRepository);

        try {
            categoriaRepository.load();
        } catch (PersistenciaException e) {
            System.err.println("[WARN] No fue posible cargar datos previos: " + e.getMessage());
        }

        if (categoriaRepository.findAllCategorias().isEmpty()) {
            DataSeeder.seed(categoriaRepository);
        }

        boolean modoConsola = args != null && Arrays.stream(args).anyMatch(a -> a.equalsIgnoreCase("--console"));
        if (modoConsola) {
            new MenuPrincipal(libroRepository, categoriaRepository).iniciar();
            guardarDatos(libroRepository);
        } else {
            SwingUtilities.invokeLater(() -> {
                MainWindow window = new MainWindow(categoriaRepository, libroRepository);
                window.setVisible(true);
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> guardarDatos(libroRepository)));
        }
    }

    private static void guardarDatos(LibroRepository libroRepository) {
        try {
            libroRepository.save();
        } catch (PersistenciaException e) {
            System.err.println("[ERROR] No se pudieron guardar los datos: " + e.getMessage());
        }
    }
}
