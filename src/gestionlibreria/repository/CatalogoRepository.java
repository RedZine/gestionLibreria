package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones esenciales para administrar el catálogo de libros.
 */
public interface CatalogoRepository {

    /**
     * Recupera todos los libros almacenados.
     *
     * @return lista con todos los libros disponibles
     */
    List<Libro> obtenerTodos();

    /**
     * Busca un libro utilizando su ISBN.
     *
     * @param isbn código del libro a buscar
     * @return libro encontrado o vacío cuando no existe
     */
    Optional<Libro> buscarPorIsbn(String isbn);

    /**
     * Registra un nuevo libro en el catálogo.
     *
     * @param libro libro a agregar
     */
    void add(Libro libro);

    /**
     * Actualiza los datos de un libro existente.
     *
     * @param libro libro con la información actualizada
     */
    void update(Libro libro);

    /**
     * Elimina un libro del catálogo por su ISBN.
     *
     * @param isbn código del libro a eliminar
     */
    void deleteByIsbn(String isbn);
}
