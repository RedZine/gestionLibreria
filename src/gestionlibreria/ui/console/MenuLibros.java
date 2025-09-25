package gestionlibreria.ui.console;

import gestionlibreria.model.Libro;
import gestionlibreria.repository.CatalogoRepository;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import gestionlibreria.util.EntradaConsolaUtil;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu de operaciones sobre el catálogo de libros.
 */
public class MenuLibros {
    private final CatalogoRepository catalogoRepository;
    private final Scanner scanner;

    public MenuLibros(CatalogoRepository catalogoRepository, Scanner scanner) {
        this.catalogoRepository = catalogoRepository;
        this.scanner = scanner;
    }

    /**
     * Despliega el menú de libros hasta que el usuario regrese al menú anterior.
     */
    public void mostrar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gestión de Libros ---");
            System.out.println("1. Listar libros");
            System.out.println("2. Agregar libro");
            System.out.println("3. Editar libro");
            System.out.println("4. Eliminar libro");
            System.out.println("5. Buscar por ISBN");
            System.out.println("6. Filtrar por rango de precios");
            System.out.println("0. Volver");
            String opcion = EntradaConsolaUtil.leerTexto(scanner, "Seleccione opción: ");
            switch (opcion) {
                case "1":
                    listarLibros();
                    break;
                case "2":
                    agregarLibro();
                    break;
                case "3":
                    editarLibro();
                    break;
                case "4":
                    eliminarLibro();
                    break;
                case "5":
                    buscarLibro();
                    break;
                case "6":
                    filtrarPorPrecio();
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void listarLibros() {
        List<Libro> libros = catalogoRepository.obtenerTodos();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        libros.forEach(System.out::println);
    }

    private void agregarLibro() {
        System.out.println("\n--- Nuevo Libro ---");
        String titulo = EntradaConsolaUtil.leerTexto(scanner, "Título: ");
        String isbn = EntradaConsolaUtil.leerTexto(scanner, "ISBN: ");
        String autor = EntradaConsolaUtil.leerTexto(scanner, "Autor: ");
        String categoria = EntradaConsolaUtil.leerTexto(scanner, "Categoría: ");
        String idioma = EntradaConsolaUtil.leerTexto(scanner, "Idioma: ");
        int anio = EntradaConsolaUtil.leerEntero(scanner, "Año de publicación: ");
        int paginas = EntradaConsolaUtil.leerEntero(scanner, "Páginas: ");
        String editorial = EntradaConsolaUtil.leerTexto(scanner, "Editorial: ");
        int edicion = EntradaConsolaUtil.leerEntero(scanner, "Edición: ");
        double precio = EntradaConsolaUtil.leerDecimal(scanner, "Precio: ");
        int stock = EntradaConsolaUtil.leerEntero(scanner, "Stock: ");
        Libro libro = new Libro(titulo, isbn, autor, categoria, idioma, anio, paginas, editorial, edicion, precio, stock);
        try {
            catalogoRepository.add(libro);
            System.out.println("Libro agregado correctamente.");
        } catch (DatoObligatorioException | EntidadNoEncontradaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void editarLibro() {
        System.out.println("\n--- Editar Libro ---");
        String isbn = EntradaConsolaUtil.leerTexto(scanner, "ISBN del libro: ");
        Optional<Libro> libroOpt = catalogoRepository.buscarPorIsbn(isbn);
        if (libroOpt.isEmpty()) {
            System.out.println("No se encontró el libro.");
            return;
        }
        Libro original = libroOpt.get();
        String titulo = EntradaConsolaUtil.leerTexto(scanner, "Título (actual: " + original.getTitulo() + "): ");
        String autor = EntradaConsolaUtil.leerTexto(scanner, "Autor (actual: " + original.getAutor() + "): ");
        String categoria = EntradaConsolaUtil.leerTexto(scanner, "Categoría (actual: " + original.getCategoria() + "): ");
        String idioma = EntradaConsolaUtil.leerTexto(scanner, "Idioma (actual: " + original.getIdioma() + "): ");
        int anio = EntradaConsolaUtil.leerEntero(scanner, "Año de publicación (actual: " + original.getAnioPublicacion() + "): ");
        int paginas = EntradaConsolaUtil.leerEntero(scanner, "Páginas (actual: " + original.getPaginas() + "): ");
        String editorial = EntradaConsolaUtil.leerTexto(scanner, "Editorial (actual: " + original.getEditorial() + "): ");
        int edicion = EntradaConsolaUtil.leerEntero(scanner, "Edición (actual: " + original.getEdicion() + "): ");
        double precio = EntradaConsolaUtil.leerDecimal(scanner, "Precio (actual: " + original.getPrecio() + "): ");
        int stock = EntradaConsolaUtil.leerEntero(scanner, "Stock (actual: " + original.getStock() + "): ");
        Libro actualizado = new Libro(titulo, isbn, autor, categoria, idioma, anio, paginas, editorial, edicion, precio, stock);
        try {
            catalogoRepository.update(actualizado);
            System.out.println("Libro actualizado.");
        } catch (DatoObligatorioException | EntidadNoEncontradaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void eliminarLibro() {
        System.out.println("\n--- Eliminar Libro ---");
        String isbn = EntradaConsolaUtil.leerTexto(scanner, "ISBN del libro: ");
        try {
            catalogoRepository.deleteByIsbn(isbn);
            System.out.println("Libro eliminado.");
        } catch (DatoObligatorioException | EntidadNoEncontradaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void buscarLibro() {
        String isbn = EntradaConsolaUtil.leerTexto(scanner, "ISBN a buscar: ");
        Optional<Libro> libroOpt = catalogoRepository.buscarPorIsbn(isbn);
        libroOpt.ifPresentOrElse(System.out::println, () -> System.out.println("No se encontró el libro."));
    }

    private void filtrarPorPrecio() {
        double minimo = EntradaConsolaUtil.leerDecimal(scanner, "Precio mínimo: ");
        double maximo = EntradaConsolaUtil.leerDecimal(scanner, "Precio máximo: ");
        if (maximo < minimo) {
            System.out.println("El precio máximo debe ser mayor o igual al mínimo.");
            return;
        }
        List<Libro> filtrados = catalogoRepository.filtrarPorPrecio(minimo, maximo);
        if (filtrados.isEmpty()) {
            System.out.println("No se encontraron libros en el rango indicado.");
            return;
        }
        filtrados.forEach(System.out::println);
    }
}
