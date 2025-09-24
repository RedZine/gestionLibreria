package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Implementación que persiste los datos en archivos CSV sencillos.
 * Cumple con el requerimiento SIA2.2 de carga/grabado batch.
 */
public class CsvCategoriaRepository extends InMemoryCategoriaRepository {

    private final Path directorioDatos;
    private final Path categoriasCsv;
    private final Path librosCsv;
    private final DecimalFormat decimalFormat;

    public CsvCategoriaRepository() {
        this(Paths.get("data"));
    }

    public CsvCategoriaRepository(Path directorioDatos) {
        this.directorioDatos = directorioDatos;
        this.categoriasCsv = directorioDatos.resolve("categorias.csv");
        this.librosCsv = directorioDatos.resolve("libros.csv");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        this.decimalFormat = new DecimalFormat("0.##", symbols);
    }

    @Override
    public synchronized void load() throws PersistenciaException {
        limpiarTodo();
        try {
            Files.createDirectories(directorioDatos);
            cargarCategorias();
            cargarLibros();
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar datos desde CSV", e);
        }
    }

    @Override
    public synchronized void save() throws PersistenciaException {
        try {
            Files.createDirectories(directorioDatos);
            guardarCategorias();
            guardarLibros();
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar datos en CSV", e);
        }
    }

    private void cargarCategorias() throws IOException, PersistenciaException {
        if (!Files.exists(categoriasCsv)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(categoriasCsv, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#") || linea.startsWith("id;")) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                if (partes.length < 2) {
                    continue;
                }
                try {
                    int id = Integer.parseInt(partes[0].trim());
                    String nombre = partes[1].trim();
                    super.addCategoria(new Categoria(id, nombre));
                } catch (NumberFormatException | DatoDuplicadoException e) {
                    throw new PersistenciaException("Error al procesar categoría: " + linea, e);
                }
            }
        }
    }

    private void cargarLibros() throws IOException, PersistenciaException {
        if (!Files.exists(librosCsv)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(librosCsv, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#") || linea.startsWith("categoriaId;")) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                if (partes.length < 7) {
                    continue;
                }
                try {
                    int categoriaId = Integer.parseInt(partes[0].trim());
                    String isbn = partes[1].trim();
                    String titulo = partes[2].trim();
                    String autor = partes[3].trim();
                    String categoriaNombre = partes[4].trim();
                    double precio = Double.parseDouble(partes[5].trim());
                    int stock = Integer.parseInt(partes[6].trim());

                    Libro libro = new Libro(isbn, titulo, autor, categoriaNombre, precio, stock, categoriaId);
                    try {
                        super.addLibro(categoriaId, libro);
                    } catch (DatoDuplicadoException e) {
                        // Si hay duplicados se ignoran ya que priorizamos el primero encontrado.
                    }
                } catch (NumberFormatException | ElementoNoEncontradoException e) {
                    throw new PersistenciaException("Error al procesar libro: " + linea, e);
                }
            }
        }
    }

    private void guardarCategorias() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(categoriasCsv, StandardCharsets.UTF_8)) {
            writer.write("id;nombre");
            writer.newLine();
            for (Categoria categoria : categorias) {
                writer.write(categoria.getId() + ";" + limpiarSeparadores(categoria.getNombre()));
                writer.newLine();
            }
        }
    }

    private void guardarLibros() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(librosCsv, StandardCharsets.UTF_8)) {
            writer.write("categoriaId;isbn;titulo;autor;categoria;precio;stock");
            writer.newLine();
            for (Categoria categoria : categorias) {
                for (Libro libro : categoria.getLibros()) {
                    String linea = categoria.getId() + ";" +
                            limpiarSeparadores(libro.getIsbn()) + ";" +
                            limpiarSeparadores(libro.getTitulo()) + ";" +
                            limpiarSeparadores(libro.getAutor()) + ";" +
                            limpiarSeparadores(libro.getCategoria()) + ";" +
                            decimalFormat.format(libro.getPrecio()) + ";" +
                            libro.getStock();
                    writer.write(linea);
                    writer.newLine();
                }
            }
        }
    }

    private String limpiarSeparadores(String valor) {
        if (valor == null) return "";
        return valor.replace(';', ',');
    }
}
