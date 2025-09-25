package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
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

    void agregar(Libro libro) throws DatoDuplicadoException;

    void actualizar(Libro libro) throws DatoNoEncontradoException;

    void eliminar(String isbn) throws DatoNoEncontradoException;

    List<Libro> filtrarPorPrecio(double minimo, double maximo);
}
