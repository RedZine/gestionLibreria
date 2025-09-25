package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria del catálogo de libros.
 */
public class InMemoryLibroRepository implements CatalogoRepository {
    private final List<Libro> libros = new ArrayList<>();

    @Override
    public List<Libro> obtenerTodos() {
        // Entrega una copia inmutable para no exponer la lista interna
        return Collections.unmodifiableList(libros);
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libros.stream()
                .filter(libro -> libro.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    @Override
    public void add(Libro libro) {
        validarLibro(libro);
        // Evita duplicar libros con el mismo ISBN
        if (buscarPorIsbn(libro.getIsbn()).isPresent()) {
            throw new DatoObligatorioException("ISBN duplicado: " + libro.getIsbn());
        }
        libros.add(libro);
    }

    @Override
    public void update(Libro libro) {
        validarLibro(libro);
        Libro existente = buscarPorIsbn(libro.getIsbn())
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el ISBN: " + libro.getIsbn()));
        existente.setTitulo(libro.getTitulo());
        existente.setAutor(libro.getAutor());
        existente.setCategoria(libro.getCategoria());
        existente.setIdioma(libro.getIdioma());
        existente.setAnioPublicacion(libro.getAnioPublicacion());
        existente.setPaginas(libro.getPaginas());
        existente.setEditorial(libro.getEditorial());
        existente.setEdicion(libro.getEdicion());
        existente.setPrecio(libro.getPrecio());
        existente.setStock(libro.getStock());
    }

    @Override
    public void deleteByIsbn(String isbn) {
        validarIsbn(isbn);
        Libro existente = buscarPorIsbn(isbn)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el ISBN: " + isbn));
        libros.remove(existente);
    }

    private void validarLibro(Libro libro) {
        if (libro == null) {
            throw new DatoObligatorioException("El libro es obligatorio.");
        }
        // Verifica que el ISBN sea obligatorio
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        }
        // Verifica que el título sea obligatorio
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new DatoObligatorioException("El título es obligatorio.");
        }
        // Verifica que el autor sea obligatorio
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new DatoObligatorioException("El autor es obligatorio.");
        }
        // Verifica que el precio no sea negativo
        if (libro.getPrecio() < 0) {
            throw new DatoObligatorioException("El precio no puede ser negativo.");
        }
        // Verifica que el stock no sea negativo
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
