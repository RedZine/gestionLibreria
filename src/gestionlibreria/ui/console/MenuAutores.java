package gestionlibreria.ui.console;

import gestionlibreria.model.Libro;
import gestionlibreria.repository.LibroRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MenuAutores {
    private final LibroRepository repositorio;
    private final Scanner consola = new Scanner(System.in);

    public MenuAutores(LibroRepository repositorio) { this.repositorio = repositorio; }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== AUTORES ===");
            System.out.println("1) Listar autores únicos");
            System.out.println("2) Mostrar libros de un autor (por texto en nombre)");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: listarAutoresUnicos(); break;
                case 2: mostrarLibrosDeAutor(); break;
                case 0: break;
                default: System.out.println("Opción inválida."); break;
            }
        } while (opcion != 0);
    }

    private void listarAutoresUnicos() {
        List<Libro> libros = repositorio.findAll();
        if (libros.isEmpty()) { System.out.println("No hay libros cargados."); return; }

        Set<String> autores = new LinkedHashSet<String>();
        for (Libro l : libros) {
            String autor = l.getAutor();
            if (autor != null && !autor.trim().isEmpty()) autores.add(autor.trim());
        }
        if (autores.isEmpty()) { System.out.println("No hay autores asociados."); return; }

        System.out.println("Autores:");
        for (String nombreAutor : autores) System.out.println(" - " + nombreAutor);
    }

    private void mostrarLibrosDeAutor() {
        String criterio = leerTexto("Buscar (parte del nombre del autor): ").toLowerCase();
        if (criterio.isEmpty()) { System.out.println("Texto de búsqueda obligatorio."); return; }

        List<Libro> libros = repositorio.findAll();
        int encontrados = 0;
        for (Libro l : libros) {
            String autor = l.getAutor() == null ? "" : l.getAutor().trim().toLowerCase();
            if (autor.contains(criterio)) {
                System.out.println(l.getIsbn() + " | " + l.getTitulo() + " | " + l.getAutor());
                encontrados++;
            }
        }
        if (encontrados == 0) System.out.println("No se encontraron libros para ese autor.");
    }

    // utilidades
    private String leerTexto(String mensaje) { System.out.print(mensaje); return consola.nextLine().trim(); }
    private int leerEntero() {
        while (true) {
            try { return Integer.parseInt(consola.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.print("Ingrese un número entero válido: "); }
        }
    }
}
