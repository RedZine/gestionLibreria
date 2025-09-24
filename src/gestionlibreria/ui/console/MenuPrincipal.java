package gestionlibreria.ui.console;

import gestionlibreria.repository.LibroRepository;
import java.util.Scanner;

public class MenuPrincipal {
    private final Scanner consola = new Scanner(System.in);
    private final LibroRepository repositorio; // <-- nuevo
    private final MenuLibros menuLibros;
    private final MenuAutores menuAutores;

    public MenuPrincipal(LibroRepository repositorio) {
        this.repositorio = repositorio;       // <-- nuevo
        this.menuLibros  = new MenuLibros(repositorio);
        this.menuAutores = new MenuAutores(repositorio);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1) Gestionar Libros");
            System.out.println("2) Consultas de Autores");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: menuLibros.iniciar();  break;
                case 2: menuAutores.iniciar(); break;
                case 0:
                    System.out.println("Guardando datos en CSV...");
                    repositorio.save(); // <-- aquí verás el mensaje de [CSV] Guardados...
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
