package gestionlibreria.repository;

import gestionlibreria.config.AppConfig;
import gestionlibreria.model.Libro;
import gestionlibreria.util.ArchivoUtil;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación basada en archivo CSV para el catálogo de libros.
 */
public class ArchivoCatalogoRepository implements CatalogoRepository {
    private final List<Libro> libros = new ArrayList<>();

    @Override
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

    @Override
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
    public void agregar(Libro libro) throws DatoDuplicadoException {
        if (buscarPorIsbn(libro.getIsbn()).isPresent()) {
            throw new DatoDuplicadoException("Ya existe un libro con ISBN " + libro.getIsbn());
        }
        libros.add(libro);
    }

    @Override
    public void actualizar(Libro libro) throws DatoNoEncontradoException {
        Optional<Libro> existente = buscarPorIsbn(libro.getIsbn());
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró el libro con ISBN " + libro.getIsbn());
        }
        Libro original = existente.get();
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
    public void eliminar(String isbn) throws DatoNoEncontradoException {
        Optional<Libro> existente = buscarPorIsbn(isbn);
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró el libro con ISBN " + isbn);
        }
        libros.remove(existente.get());
    }

    @Override
    public List<Libro> filtrarPorPrecio(double minimo, double maximo) {
        List<Libro> filtrados = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getPrecio() >= minimo && libro.getPrecio() <= maximo) {
                filtrados.add(libro);
            }
        }
        return filtrados;
    }
}
