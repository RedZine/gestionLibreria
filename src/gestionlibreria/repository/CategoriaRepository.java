package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio que administra la colección principal de categorías
 * y la colección anidada de libros asociada a cada categoría.
 */
public interface CategoriaRepository {

    // ---- Categorías (1er nivel) ----
    List<Categoria> findAllCategorias();
    Optional<Categoria> findCategoriaById(int id);
    Optional<Categoria> findCategoriaByNombre(String nombre);
    Categoria addCategoria(Categoria categoria) throws DatoDuplicadoException;
    void updateCategoria(Categoria categoria) throws ElementoNoEncontradoException, DatoDuplicadoException;
    void deleteCategoria(int id) throws ElementoNoEncontradoException;

    // ---- Libros (2º nivel) ----
    List<Libro> findLibrosByCategoria(int categoriaId) throws ElementoNoEncontradoException;
    void addLibro(int categoriaId, Libro libro) throws DatoDuplicadoException, ElementoNoEncontradoException;
    void updateLibro(int categoriaId, Libro libro) throws ElementoNoEncontradoException;
    void deleteLibro(int categoriaId, String isbn) throws ElementoNoEncontradoException;

    // ---- Operaciones transversales ----
    List<Libro> findAllLibros();
    Optional<Libro> findLibroByIsbn(String isbn);
    List<Libro> filterLibrosByPrecio(double minimo, double maximo);
    List<Libro> filterLibrosByCategoria(String categoria);
    List<Libro> filterLibrosByAutor(String autor);
    List<Libro> filterLibrosByStock(int stockMinimo, int stockMaximo);

    // ---- Persistencia Batch ----
    void load() throws PersistenciaException;
    void save() throws PersistenciaException;
}
