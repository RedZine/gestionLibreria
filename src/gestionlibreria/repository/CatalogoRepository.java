package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de acceso a los libros del catálogo.
 */
public interface CatalogoRepository {
    void cargar() throws IOException;

    void guardar() throws IOException;

    List<Libro> obtenerTodos();

    Optional<Libro> buscarPorIsbn(String isbn);

    void add(Libro libro) throws DatoObligatorioException;

    void update(Libro libro) throws EntidadNoEncontradaException;

    void deleteByIsbn(String isbn) throws EntidadNoEncontradaException;

    List<Libro> filtrarPorPrecio(double minimo, double maximo);
}
