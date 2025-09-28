package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que permite reutilizar el repositorio de categorías como
 * fuente de verdad para las operaciones de libros.
 */
public class CategoriaLibroRepository implements LibroRepository {

    private final CategoriaRepository categoriaRepository;

    public CategoriaLibroRepository(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Libro> findAll() {
        return categoriaRepository.findAllLibros();
    }

    @Override
    public Optional<Libro> findByIsbn(String isbn) {
        return categoriaRepository.findLibroByIsbn(isbn);
    }

    @Override
    public void add(Libro libro) throws DatoDuplicadoException, ElementoNoEncontradoException {
        int categoriaId = obtenerCategoriaId(libro);
        categoriaRepository.addLibro(categoriaId, libro);
    }
    @Override
    public void update(Libro libro) throws ElementoNoEncontradoException {
    // Resuelve y normaliza la categoría indicada en el formulario
        int nuevaCategoriaId = obtenerCategoriaId(libro);

    // Busca el libro existente por ISBN para saber su categoría actual
        Libro existente = categoriaRepository.findLibroByIsbn(libro.getIsbn())
            .orElseThrow(() -> new ElementoNoEncontradoException("No existe el ISBN: " + libro.getIsbn()));

        int categoriaActualId = existente.getCategoriaId();

        if (categoriaActualId != nuevaCategoriaId) {
        // Si cambió la categoría, hacemos un "mover": borrar del viejo y agregar al nuevo
            try {
            categoriaRepository.deleteLibro(categoriaActualId, existente.getIsbn());
            categoriaRepository.addLibro(nuevaCategoriaId, libro);
            } catch (DatoDuplicadoException e) {
            // En teoría no debería pasar si el ISBN es único; propagamos como 404 lógico
                throw new ElementoNoEncontradoException(e.getMessage());
            }
        }else {
        // Si la categoría no cambió, actualizamos en la misma
            categoriaRepository.updateLibro(nuevaCategoriaId, libro);
        }
}
    
    
    


    @Override
    public void deleteByIsbn(String isbn) throws ElementoNoEncontradoException {
        Libro existente = categoriaRepository.findLibroByIsbn(isbn)
                .orElseThrow(() -> new ElementoNoEncontradoException("No existe el ISBN: " + isbn));
        categoriaRepository.deleteLibro(existente.getCategoriaId(), isbn);
    }

    @Override
    public List<Libro> filterByPrecio(double minimo, double maximo) {
        return categoriaRepository.filterLibrosByPrecio(minimo, maximo);
    }

    @Override
    public List<Libro> filterByCategoria(String categoria) {
        return categoriaRepository.filterLibrosByCategoria(categoria);
    }

    @Override
    public List<Libro> filterByAutor(String autorContiene) {
        return categoriaRepository.filterLibrosByAutor(autorContiene);
    }

    @Override
    public List<Libro> filterByStock(int stockMinimo, int stockMaximo) {
        return categoriaRepository.filterLibrosByStock(stockMinimo, stockMaximo);
    }

    @Override
    public void load() throws PersistenciaException {
        categoriaRepository.load();
    }

    @Override
    public void save() throws PersistenciaException {
        categoriaRepository.save();
    }

    private int obtenerCategoriaId(Libro libro) throws ElementoNoEncontradoException {
        int categoriaId = libro.getCategoriaId();
        if (categoriaId > 0) {
            return categoriaId;
        }
        String nombreCategoria = libro.getCategoria();
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            throw new ElementoNoEncontradoException("Debe indicar la categoría del libro");
        }
        Optional<Categoria> categoria = categoriaRepository.findCategoriaByNombre(nombreCategoria);
        if (categoria.isEmpty()) {
            throw new ElementoNoEncontradoException("Categoría no encontrada: " + nombreCategoria);
        }
        libro.setCategoriaId(categoria.get().getId());
        libro.setCategoria(categoria.get().getNombre());
        return categoria.get().getId();
    }
}
