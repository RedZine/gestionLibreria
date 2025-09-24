// src/gestionlibreria/ui/console/MenuLibros.java
package gestionlibreria.ui.console;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.LibroRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuLibros {
    private final LibroRepository repositorio;
    private final CategoriaRepository categoriaRepository;
    private final Scanner consola = new Scanner(System.in);

    public MenuLibros(LibroRepository repositorio, CategoriaRepository categoriaRepository) {
        this.repositorio = repositorio;
        this.categoriaRepository = categoriaRepository;
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
            System.out.println("8) Filtrar por stock");
            System.out.println("0) Volver");
            System.out.print("Opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: listar(); break;
                case 2: agregar(); break;
                case 3: editar(); break;
                case 4: eliminar(); break;
                case 5: buscarPorIsbn(); break;
                case 6: filtrarPorPrecio(); break;
                case 7: filtrarPorCategoria(); break;
                case 8: filtrarPorStock(); break;
                case 0: break;
                default: System.out.println("Opción inválida."); break;
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
        Categoria categoria = seleccionarCategoria();
        if (categoria == null) {
            System.out.println("Debe crear categorías antes de agregar libros.");
            return;
        }
        String isbn       = leerTextoObligatorio("ISBN: ");
        String titulo     = leerTextoObligatorio("Título: ");
        String autor      = leerTextoObligatorio("Autor (nombre): ");
        double precio     = leerDouble("Precio: ");
        int stock         = leerEnteroConMensaje("Stock: ");

        try {
            Libro nuevo = new Libro();
            nuevo.setIsbn(isbn);
            nuevo.setTitulo(titulo);
            nuevo.setAutor(autor);
            nuevo.setCategoria(categoria.getNombre());
            nuevo.setCategoriaId(categoria.getId());
            nuevo.setPrecio(precio);
            nuevo.setStock(stock);

            repositorio.add(nuevo);
            System.out.println("Libro agregado.");
        } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
            System.out.println("Error al agregar: " + e.getMessage());
        }
    }

    private void editar() {
        System.out.println("\n=== Editar libro ===");
        String isbn = leerTextoObligatorio("ISBN a editar: ");
        Optional<Libro> existente = repositorio.findByIsbn(isbn);
        if (existente.isEmpty()) {
            System.out.println("No existe ese ISBN.");
            return;
        }
        Libro libro = existente.get();

        String nuevoTitulo    = leerTexto("Nuevo título (Enter mantiene: " + textoONulo(libro.getTitulo()) + "): ");
        String nuevoAutor     = leerTexto("Nuevo autor (Enter mantiene: " + textoONulo(libro.getAutor()) + "): ");
        Categoria nuevaCategoria = seleccionarCategoriaOpcional(libro);
        Double nuevoPrecio    = leerDoubleOpcional("Nuevo precio (Enter mantiene: " + libro.getPrecio() + "): ");
        Integer nuevoStock    = leerEnteroOpcional("Nuevo stock (Enter mantiene: " + libro.getStock() + "): ");

        if (!nuevoTitulo.isEmpty())    libro.setTitulo(nuevoTitulo);
        if (!nuevoAutor.isEmpty())     libro.setAutor(nuevoAutor);
        if (nuevaCategoria != null) {
            libro.setCategoria(nuevaCategoria.getNombre());
            libro.setCategoriaId(nuevaCategoria.getId());
        }
        if (nuevoPrecio != null)       libro.setPrecio(nuevoPrecio);
        if (nuevoStock != null)        libro.setStock(nuevoStock);

        try {
            repositorio.update(libro);
            System.out.println("Libro actualizado.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminar() {
        String isbn = leerTextoObligatorio("\nISBN a eliminar: ");
        try {
            repositorio.deleteByIsbn(isbn);
            System.out.println("Libro eliminado.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    private void buscarPorIsbn() {
        String isbn = leerTextoObligatorio("\nISBN a buscar: ");
        Optional<Libro> encontrado = repositorio.findByIsbn(isbn);
        if (encontrado.isEmpty()) {
            System.out.println("No encontrado.");
            return;
        }
        Libro l = encontrado.get();
        System.out.printf("Encontrado: [%s] %s | %.2f%n", l.getIsbn(), l.getTitulo(), l.getPrecio());
    }

    private void filtrarPorPrecio() {
        double minimo = leerDouble("\nPrecio mínimo: ");
        double maximo = leerDouble("Precio máximo: ");
        var resultado = repositorio.filterByPrecio(minimo, maximo);
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
        var resultado = repositorio.filterByCategoria(categoria);
        if (resultado.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }
        for (Libro l : resultado) {
            System.out.printf("%s - %s - %s%n", l.getIsbn(), l.getTitulo(), textoONulo(l.getCategoria()));
        }
    }

    private void filtrarPorStock() {
        int minimo = leerEnteroConMensaje("\nStock mínimo: ");
        int maximo = leerEnteroConMensaje("Stock máximo: ");
        var resultado = repositorio.filterByStock(minimo, maximo);
        if (resultado.isEmpty()) {
            System.out.println("Sin resultados en ese rango.");
            return;
        }
        for (Libro l : resultado) {
            System.out.printf("%s - %s - Stock: %d%n", l.getIsbn(), l.getTitulo(), l.getStock());
        }
    }

    // ---------- utilidades de lectura (claras y en español) ----------

    /** Lee una línea de texto (puede quedar vacía). */
    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return consola.nextLine().trim();
    }

    /** Lee texto obligatorio; repite hasta que no esté vacío. */
    private String leerTextoObligatorio(String mensaje) {
        while (true) {
            String textoIngresado = leerTexto(mensaje);
            if (!textoIngresado.isEmpty()) return textoIngresado;
            System.out.println("Este campo es obligatorio.");
        }
    }

    /** Lee un entero sin mensaje (para menús). */
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

    /** Lee un entero mostrando un mensaje primero. */
    private int leerEnteroConMensaje(String mensaje) {
        System.out.print(mensaje);
        return leerEntero();
    }

    /** Lee un entero opcional: Enter = null; si falla, no cambia. */
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

    /** Lee un decimal (double). */
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

    /** Lee un decimal opcional: Enter = null; si falla, no cambia. */
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

    /** Devuelve "" si es null (para imprimir sin NPE). */
    private static String textoONulo(String valor) {
        return (valor == null) ? "" : valor;
    }

    /** Alias de compatibilidad si ya usabas nvl(). */
    private static String nvl(String valor) {
        return textoONulo(valor);
    }

    private Categoria seleccionarCategoria() {
        var categorias = categoriaRepository.findAllCategorias();
        if (categorias.isEmpty()) {
            return null;
        }
        System.out.println("Categorías disponibles:");
        for (Categoria c : categorias) {
            System.out.printf("%d) %s%n", c.getId(), c.getNombre());
        }
        while (true) {
            int id = leerEnteroConMensaje("Seleccione ID de categoría: ");
            Optional<Categoria> categoria = categoriaRepository.findCategoriaById(id);
            if (categoria.isPresent()) {
                return categoria.get();
            }
            System.out.println("ID inválido, intente nuevamente.");
        }
    }

    private Categoria seleccionarCategoriaOpcional(Libro libro) {
        var categorias = categoriaRepository.findAllCategorias();
        if (categorias.isEmpty()) {
            return null;
        }
        System.out.println("Categorías disponibles (Enter mantiene actual):");
        for (Categoria c : categorias) {
            System.out.printf("%d) %s%n", c.getId(), c.getNombre());
        }
        System.out.print("Ingrese ID de categoría o Enter para mantener: ");
        String entrada = consola.nextLine().trim();
        if (entrada.isEmpty()) {
            return categoriaRepository.findCategoriaById(libro.getCategoriaId()).orElse(null);
        }
        try {
            int id = Integer.parseInt(entrada);
            return categoriaRepository.findCategoriaById(id).orElse(null);
        } catch (NumberFormatException ex) {
            System.out.println("Valor inválido, se mantiene la categoría actual.");
            return categoriaRepository.findCategoriaById(libro.getCategoriaId()).orElse(null);
        }
    }
}
