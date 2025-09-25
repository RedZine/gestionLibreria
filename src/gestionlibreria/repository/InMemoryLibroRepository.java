package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria para operar con el catálogo de libros.
 */
public class InMemoryLibroRepository implements CatalogoRepository {
    private final List<Libro> libros = new ArrayList<>();

    @Override
    public void cargar() throws IOException {
        // No requiere acciones porque los datos viven en memoria
    }

    @Override
    public void guardar() throws IOException {
        // No requiere acciones porque los datos viven en memoria
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
    public void add(Libro libro) throws DatoObligatorioException {
        validarLibro(libro);
        // Valida que no exista otro libro con el mismo ISBN
        if (buscarPorIsbn(libro.getIsbn()).isPresent()) {
            throw new DatoObligatorioException("ISBN duplicado: " + libro.getIsbn());
        }
        libros.add(libro);
    }

    @Override
    public void update(Libro libro) throws EntidadNoEncontradaException {
        validarLibro(libro);
        Optional<Libro> existente = buscarPorIsbn(libro.getIsbn());
        if (existente.isEmpty()) {
            throw new EntidadNoEncontradaException("No existe el ISBN: " + libro.getIsbn());
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
    public void deleteByIsbn(String isbn) throws EntidadNoEncontradaException {
        validarIsbn(isbn);
        Optional<Libro> existente = buscarPorIsbn(isbn);
        if (existente.isEmpty()) {
            throw new EntidadNoEncontradaException("No existe el ISBN: " + isbn);
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

    private void validarLibro(Libro libro) {
        // Valida que el ISBN no esté vacío
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        }
        // Valida que el título no esté vacío
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new DatoObligatorioException("El título es obligatorio.");
        }
        // Valida que el autor no esté vacío
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new DatoObligatorioException("El autor es obligatorio.");
        }
        // Valida que el precio no sea negativo
        if (libro.getPrecio() < 0) {
            throw new DatoObligatorioException("El precio no puede ser negativo.");
        }
        // Valida que el stock no sea negativo
        if (libro.getStock() < 0) {
            throw new DatoObligatorioException("El stock no puede ser negativo.");
        }
    }

    private void validarIsbn(String isbn) {
        // Valida que el ISBN recibido no esté vacío
        if (isbn == null || isbn.isBlank()) {
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        }
    }
}
