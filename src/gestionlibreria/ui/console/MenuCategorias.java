package gestionlibreria.ui.console;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú dedicado a gestionar la colección principal (categorías)
 * y la colección anidada (libros por categoría).
 */
public class MenuCategorias {
    private final CategoriaRepository categoriaRepository;
    private final Scanner consola = new Scanner(System.in);

    public MenuCategorias(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== CATEGORÍAS ===");
            System.out.println("1) Listar categorías");
            System.out.println("2) Agregar categoría");
            System.out.println("3) Editar categoría");
            System.out.println("4) Eliminar categoría");
            System.out.println("5) Gestionar libros de una categoría");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: listarCategorias(); break;
                case 2: agregarCategoria(); break;
                case 3: editarCategoria(); break;
                case 4: eliminarCategoria(); break;
                case 5: gestionarLibrosDeCategoria(); break;
                case 0: break;
                default: System.out.println("Opción inválida."); break;
            }
        } while (opcion != 0);
    }

    private void listarCategorias() {
        List<Categoria> categorias = categoriaRepository.findAllCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        System.out.println("ID | Nombre | Cantidad de libros");
        for (Categoria c : categorias) {
            int cantidadLibros = c.getLibros().size();
            System.out.printf("%d | %s | %d%n", c.getId(), c.getNombre(), cantidadLibros);
        }
    }

    private void agregarCategoria() {
        String nombre = leerTextoObligatorio("Nombre de la categoría: ");
        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoriaRepository.addCategoria(categoria);
            System.out.println("Categoría agregada con éxito.");
        } catch (DatoDuplicadoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editarCategoria() {
        listarCategorias();
        int id = leerEnteroConMensaje("ID de la categoría a editar: ");
        Optional<Categoria> categoriaOpt = categoriaRepository.findCategoriaById(id);
        if (categoriaOpt.isEmpty()) {
            System.out.println("Categoría no encontrada.");
            return;
        }
        String nuevoNombre = leerTextoObligatorio("Nuevo nombre: ");
        Categoria categoria = new Categoria(id, nuevoNombre);
        try {
            categoriaRepository.updateCategoria(categoria);
            System.out.println("Categoría actualizada.");
        } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarCategoria() {
        listarCategorias();
        int id = leerEnteroConMensaje("ID de la categoría a eliminar: ");
        try {
            categoriaRepository.deleteCategoria(id);
            System.out.println("Categoría eliminada.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void gestionarLibrosDeCategoria() {
        listarCategorias();
        int id = leerEnteroConMensaje("ID de la categoría: ");
        Optional<Categoria> categoriaOpt = categoriaRepository.findCategoriaById(id);
        if (categoriaOpt.isEmpty()) {
            System.out.println("Categoría no encontrada.");
            return;
        }
        Categoria categoria = categoriaOpt.get();
        int opcion;
        do {
            System.out.println("\n=== Libros de " + categoria.getNombre() + " ===");
            System.out.println("1) Listar libros");
            System.out.println("2) Agregar libro");
            System.out.println("3) Editar libro");
            System.out.println("4) Eliminar libro");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: listarLibros(categoria); break;
                case 2: agregarLibro(categoria); break;
                case 3: editarLibro(categoria); break;
                case 4: eliminarLibro(categoria); break;
                case 0: break;
                default: System.out.println("Opción inválida."); break;
            }
        } while (opcion != 0);
    }

    private void listarLibros(Categoria categoria) {
        List<Libro> libros = categoria.getLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros asociados.");
            return;
        }
        for (Libro libro : libros) {
            System.out.printf("%s | %s | %s | %.2f | %d%n",
                    libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getPrecio(), libro.getStock());
        }
    }

    private void agregarLibro(Categoria categoria) {
        String isbn = leerTextoObligatorio("ISBN: ");
        String titulo = leerTextoObligatorio("Título: ");
        String autor = leerTextoObligatorio("Autor: ");
        double precio = leerDouble("Precio: ");
        int stock = leerEnteroConMensaje("Stock: ");

        Libro libro = new Libro(isbn, titulo, autor, categoria.getNombre(), precio, stock, categoria.getId());
        try {
            categoriaRepository.addLibro(categoria.getId(), libro);
            System.out.println("Libro agregado.");
        } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editarLibro(Categoria categoria) {
        String isbn = leerTextoObligatorio("ISBN del libro a editar: ");
        Optional<Libro> libroOpt = categoriaRepository.findLibroByIsbn(isbn);
        if (libroOpt.isEmpty() || libroOpt.get().getCategoriaId() != categoria.getId()) {
            System.out.println("Libro no encontrado en esta categoría.");
            return;
        }
        Libro libro = libroOpt.get();
        String nuevoTitulo = leerTexto("Nuevo título (Enter mantiene): ");
        String nuevoAutor = leerTexto("Nuevo autor (Enter mantiene): ");
        Double nuevoPrecio = leerDoubleOpcional("Nuevo precio (Enter mantiene): ");
        Integer nuevoStock = leerEnteroOpcional("Nuevo stock (Enter mantiene): ");

        if (!nuevoTitulo.isEmpty()) libro.setTitulo(nuevoTitulo);
        if (!nuevoAutor.isEmpty()) libro.setAutor(nuevoAutor);
        if (nuevoPrecio != null) libro.setPrecio(nuevoPrecio);
        if (nuevoStock != null) libro.setStock(nuevoStock);

        try {
            categoriaRepository.updateLibro(categoria.getId(), libro);
            System.out.println("Libro actualizado.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarLibro(Categoria categoria) {
        String isbn = leerTextoObligatorio("ISBN del libro a eliminar: ");
        try {
            categoriaRepository.deleteLibro(categoria.getId(), isbn);
            System.out.println("Libro eliminado.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- utilidades de lectura ---
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

    private int leerEnteroConMensaje(String mensaje) {
        System.out.print(mensaje);
        return leerEntero();
    }

    private Integer leerEnteroOpcional(String mensaje) {
        System.out.print(mensaje);
        String entrada = consola.nextLine().trim();
        if (entrada.isEmpty()) return null;
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException ex) {
            System.out.println("Valor inválido. Se mantiene el actual.");
            return null;
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String entrada = consola.nextLine().trim();
                return Double.parseDouble(entrada);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número válido (ej: 12990 o 12990.50).");
            }
        }
    }

    private Double leerDoubleOpcional(String mensaje) {
        System.out.print(mensaje);
        String entrada = consola.nextLine().trim();
        if (entrada.isEmpty()) return null;
        try {
            return Double.parseDouble(entrada);
        } catch (NumberFormatException ex) {
            System.out.println("Valor inválido. Se mantiene el actual.");
            return null;
        }
    }

    private String leerTextoObligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = consola.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Este campo es obligatorio.");
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return consola.nextLine().trim();
    }
}
