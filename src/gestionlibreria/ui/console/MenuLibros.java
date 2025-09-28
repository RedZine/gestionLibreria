package gestionlibreria.ui.console;

import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú de consola para gestionar libros.
 * Captura excepciones específicas y muestra mensajes claros (SIA2.8).
 */
public class MenuLibros {
    private final LibroRepository repositorio;
    private final CategoriaRepository categoriaRepositorio; // puede ser null
    private final Scanner consola = new Scanner(System.in);

    /** Constructor que usa MenuPrincipal (dos argumentos). */
    public MenuLibros(LibroRepository repositorio, CategoriaRepository categoriaRepositorio) {
        this.repositorio = repositorio;
        this.categoriaRepositorio = categoriaRepositorio; // hoy no se usa aquí
    }

    /** Sobrecarga por si en algún lado lo llaman con solo LibroRepository. */
    public MenuLibros(LibroRepository repositorio) {
        this(repositorio, null);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== LIBROS ===");
            System.out.println("1) Listar");
            System.out.println("2) Agregar");
            System.out.println("3) Editar");
            System.out.println("4) Eliminar");
            System.out.println("5) Buscar por ISBN");
            System.out.println("6) Filtrar por precio");
            System.out.println("7) Filtrar por categoría");
            System.out.println("8) Exportar reporte ahora (CSV)");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    listar();
                    break;
                case 2:
                    agregar();
                    break;
                case 3:
                    editar();
                    break;
                case 4:
                    eliminar();
                    break;
                case 5:
                    buscarPorIsbn();
                    break;
                case 6:
                    filtrarPorPrecio();
                    break;
                case 7:
                    filtrarPorCategoria();
                    break;
                case 8:
                    exportarAhora();
                    break;
                case 0:
                    // volver
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } while (opcion != 0);
    }

    private void listar() {
        List<Libro> libros = repositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros.");
            return;
        }
        System.out.println("ISBN | Título | Autor | Categoría | Precio | Stock");
        for (Libro l : libros) {
            System.out.printf("%s | %s | %s | %s | %.2f | %d%n",
                    textoONulo(l.getIsbn()),
                    textoONulo(l.getTitulo()),
                    textoONulo(l.getAutor()),
                    textoONulo(l.getCategoria()),
                    l.getPrecio(),
                    l.getStock());
        }
    }

    private void agregar() {
        System.out.println("\n=== Agregar libro ===");
        String isbn       = leerTextoObligatorio("ISBN: ");
        String titulo     = leerTextoObligatorio("Título: ");
        String autor      = leerTextoObligatorio("Autor (nombre): ");
        String categoria  = leerTextoObligatorio("Categoría: ");
        double precio     = leerDouble("Precio: ");
        int stock         = leerEnteroConMensaje("Stock: ");

        try {
            Libro nuevo = new Libro();
            nuevo.setIsbn(isbn);
            nuevo.setTitulo(titulo);
            nuevo.setAutor(autor);
            nuevo.setCategoria(categoria);
            nuevo.setPrecio(precio);
            nuevo.setStock(stock);
            repositorio.add(nuevo);
            System.out.println("Libro agregado.");
        } catch (DatoObligatorioException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private void editar() {
        System.out.println("\n=== Editar libro ===");
        String isbn = leerTextoObligatorio("ISBN a editar: ");
        Optional<Libro> existente = repositorio.findByIsbn(isbn);
        if (!existente.isPresent()) {
            System.out.println("No existe ese ISBN.");
            return;
        }
        Libro l = existente.get();

        String nuevoTitulo    = leerTexto("Nuevo título (Enter mantiene: " + textoONulo(l.getTitulo()) + "): ");
        String nuevoAutor     = leerTexto("Nuevo autor (Enter mantiene: " + textoONulo(l.getAutor()) + "): ");
        String nuevaCategoria = leerTexto("Nueva categoría (Enter mantiene: " + textoONulo(l.getCategoria()) + "): ");
        Double nuevoPrecio    = leerDoubleOpcional("Nuevo precio (Enter mantiene: " + l.getPrecio() + "): ");
        Integer nuevoStock    = leerEnteroOpcional("Nuevo stock (Enter mantiene: " + l.getStock() + "): ");

        if (!nuevoTitulo.isEmpty())    l.setTitulo(nuevoTitulo);
        if (!nuevoAutor.isEmpty())     l.setAutor(nuevoAutor);
        if (!nuevaCategoria.isEmpty()) l.setCategoria(nuevaCategoria);
        if (nuevoPrecio != null)       l.setPrecio(nuevoPrecio);
        if (nuevoStock != null)        l.setStock(nuevoStock);

        try {
            repositorio.update(l);
            System.out.println("Libro actualizado.");
        } catch (DatoObligatorioException | EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private void eliminar() {
        String isbn = leerTextoObligatorio("\nISBN a eliminar: ");
        try {
            repositorio.deleteByIsbn(isbn);
            System.out.println("Eliminado.");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private void buscarPorIsbn() {
        String isbn = leerTextoObligatorio("\nISBN a buscar: ");
        Optional<Libro> opt = repositorio.findByIsbn(isbn);
        if (!opt.isPresent()) {
            System.out.println("No encontrado.");
            return;
        }
        Libro l = opt.get();
        System.out.printf("Encontrado: [%s] %s | %.2f%n", l.getIsbn(), l.getTitulo(), l.getPrecio());
    }

    private void filtrarPorPrecio() {
        double minimo = leerDouble("\nPrecio mínimo: ");
        double maximo = leerDouble("Precio máximo: ");
        List<Libro> resultado = repositorio.filterByPrecio(minimo, maximo);
        if (resultado.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        for (Libro l : resultado) {
            System.out.printf("%s - %s - %.2f%n", l.getIsbn(), l.getTitulo(), l.getPrecio());
        }
    }

    private void filtrarPorCategoria() {
        String categoria = leerTextoObligatorio("\nCategoría (coincidencia parcial): ");
        List<Libro> resultado = repositorio.filterByCategoria(categoria);
        if (resultado.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        for (Libro l : resultado) {
            System.out.printf("%s - %s - %s%n", l.getIsbn(), l.getTitulo(), textoONulo(l.getCategoria()));
        }
    }

    private void exportarAhora() {
        try {
            repositorio.save();
            System.out.println("Reporte CSV actualizado en data/libros.csv");
        } catch (Exception e) {
            System.out.println("No se pudo exportar: " + e.getMessage());
        }
    }

    // ---------- utilidades lectura ----------
    private String leerTexto(String mensaje) { System.out.print(mensaje); return consola.nextLine().trim(); }
    private String leerTextoObligatorio(String mensaje) {
        while (true) { String s = leerTexto(mensaje); if (!s.isEmpty()) return s; System.out.println("Este campo es obligatorio."); }
    }
    private int leerEntero() {
        while (true) {
            try { return Integer.parseInt(consola.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Ingrese un número entero válido: "); }
        }
    }
    private int leerEnteroConMensaje(String m){ System.out.print(m); return leerEntero(); }
    private Integer leerEnteroOpcional(String m){
        System.out.print(m); String s=consola.nextLine().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { System.out.println("Valor inválido. Se mantiene el actual."); return null; }
    }
    private double leerDouble(String m){
        while (true) {
            try { System.out.print(m); return Double.parseDouble(consola.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número válido (ej: 12990 o 12990.50)."); }
        }
    }
    private Double leerDoubleOpcional(String m){
        System.out.print(m); String s=consola.nextLine().trim();
        if (s.isEmpty()) return null;
        try { return Double.parseDouble(s); } catch(NumberFormatException e){ System.out.println("Valor inválido. Se mantiene el actual."); return null; }
    }
    private static String textoONulo(String v){ return v==null? "" : v; }
}
