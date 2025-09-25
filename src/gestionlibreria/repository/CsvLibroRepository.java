package gestionlibreria.repository;

import gestionlibreria.config.AppConfig;
import gestionlibreria.model.Libro;
import gestionlibreria.util.ArchivoUtil;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del catálogo basada en un archivo CSV.
 */
public class CsvLibroRepository implements CatalogoRepository {
    private final List<Libro> libros = new ArrayList<>();

    /**
     * Carga los libros desde el archivo configurado.
     *
     * @throws IOException cuando no se puede leer el archivo CSV
     */
    public void cargar() throws IOException {
        libros.clear();
        List<String> lineas = ArchivoUtil.leerLineas(AppConfig.RUTA_LIBROS);
        for (String linea : lineas) {
            if (linea.isBlank()) {
                continue;
            }
            String[] datos = linea.split(";");
            try {
                Libro libro = new Libro(
                        datos[0],
                        datos[1],
                        datos[2],
                        datos[3],
                        datos[4],
                        Integer.parseInt(datos[5]),
                        Integer.parseInt(datos[6]),
                        datos[7],
                        Integer.parseInt(datos[8]),
                        Double.parseDouble(datos[9]),
                        Integer.parseInt(datos[10])
                );
                libros.add(libro);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Línea de libro inválida en CSV: " + linea);
            }
        }
    }

    /**
     * Guarda los libros en el archivo CSV.
     *
     * @throws IOException cuando no se puede escribir el archivo CSV
     */
    public void guardar() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Libro libro : libros) {
            String linea = String.join(";",
                    libro.getTitulo(),
                    libro.getIsbn(),
                    libro.getAutor(),
                    libro.getCategoria(),
                    libro.getIdioma(),
                    String.valueOf(libro.getAnioPublicacion()),
                    String.valueOf(libro.getPaginas()),
                    libro.getEditorial(),
                    String.valueOf(libro.getEdicion()),
                    String.valueOf(libro.getPrecio()),
                    String.valueOf(libro.getStock())
            );
            lineas.add(linea);
        }
        ArchivoUtil.escribirLineas(AppConfig.RUTA_LIBROS, lineas);
    }

    @Override
    public List<Libro> obtenerTodos() {
        return Collections.unmodifiableList(libros);
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libros.stream().filter(libro -> libro.getIsbn().equalsIgnoreCase(isbn)).findFirst();
    }

    @Override
    public void add(Libro libro) {
        validarLibro(libro);
        // Evita duplicar el ISBN en el archivo
        if (buscarPorIsbn(libro.getIsbn()).isPresent()) {
            throw new DatoObligatorioException("ISBN duplicado: " + libro.getIsbn());
        }
        libros.add(libro);
    }

    @Override
    public void update(Libro libro) {
        validarLibro(libro);
        Libro original = buscarPorIsbn(libro.getIsbn())
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el ISBN: " + libro.getIsbn()));
        original.setTitulo(libro.getTitulo());
        original.setAutor(libro.getAutor());
        original.setCategoria(libro.getCategoria());
        original.setIdioma(libro.getIdioma());
        original.setAnioPublicacion(libro.getAnioPublicacion());
        original.setPaginas(libro.getPaginas());
        original.setEditorial(libro.getEditorial());
        original.setEdicion(libro.getEdicion());
        original.setPrecio(libro.getPrecio());
        original.setStock(libro.getStock());
    }

    @Override
    public void deleteByIsbn(String isbn) {
        validarIsbn(isbn);
        Libro original = buscarPorIsbn(isbn)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el ISBN: " + isbn));
        libros.remove(original);
    }

    private void validarLibro(Libro libro) {
        if (libro == null) {
            throw new DatoObligatorioException("El libro es obligatorio.");
        }
        // Comprueba que el ISBN sea obligatorio
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        }
        // Comprueba que el título sea obligatorio
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new DatoObligatorioException("El título es obligatorio.");
        }
        // Comprueba que el autor sea obligatorio
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new DatoObligatorioException("El autor es obligatorio.");
        }
        // Comprueba que el precio no sea negativo
        if (libro.getPrecio() < 0) {
            throw new DatoObligatorioException("El precio no puede ser negativo.");
        }
        // Comprueba que el stock no sea negativo
        if (libro.getStock() < 0) {
            throw new DatoObligatorioException("El stock no puede ser negativo.");
        }
    }

    private void validarIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        }
    }
}
