package gestionlibreria.ui.console;

import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.LibroRepository;

import java.util.Scanner;

public class MenuPrincipal {
    private final Scanner consola = new Scanner(System.in);
    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final MenuLibros menuLibros;
    private final MenuAutores menuAutores;
    private final MenuCategorias menuCategorias;

    public MenuPrincipal(LibroRepository libroRepository, CategoriaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
        this.menuLibros  = new MenuLibros(libroRepository, categoriaRepository);
        this.menuAutores = new MenuAutores(libroRepository);
        this.menuCategorias = new MenuCategorias(categoriaRepository);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1) Gestionar Libros");
            System.out.println("2) Consultas de Autores");
            System.out.println("3) Gestionar Categorías (nivel 1 y libros anidados)");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: menuLibros.iniciar();  break;
                case 2: menuAutores.iniciar(); break;
                case 3: menuCategorias.iniciar(); break;
                case 0:
                    System.out.println("Guardando datos en CSV...");
                    try {
                        libroRepository.save();
                    } catch (PersistenciaException e) {
                        System.out.println("Error al guardar: " + e.getMessage());
                    }
                    System.out.println("Listo. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private int leerEntero() {
        while (true) {
            try {
                String entrada = consola.nextLine().trim();
                return Integer.parseInt(entrada);
            } catch (NumberFormatException ex) {
                System.out.print("Ingrese un número entero válido: ");
            }
        }
    }
}
