package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación en memoria del repositorio de categorías/libros.
 * Se utiliza tanto en modo standalone como como base para la
 * implementación CSV que realiza persistencia en disco.
 */
public class InMemoryCategoriaRepository implements CategoriaRepository {

    protected final List<Categoria> categorias = new ArrayList<>();
    protected final Map<Integer, Categoria> indicePorId = new LinkedHashMap<>();
    protected final Map<String, Categoria> indicePorNombreNormalizado = new HashMap<>();
    protected final Map<String, Libro> indiceLibros = new HashMap<>();
    protected final AtomicInteger secuenciaId = new AtomicInteger(1);

    @Override
    public synchronized List<Categoria> findAllCategorias() {
        return new ArrayList<>(categorias);
    }

    @Override
    public synchronized Optional<Categoria> findCategoriaById(int id) {
        return Optional.ofNullable(indicePorId.get(id));
    }

    @Override
    public synchronized Optional<Categoria> findCategoriaByNombre(String nombre) {
        if (nombre == null) return Optional.empty();
        return Optional.ofNullable(indicePorNombreNormalizado.get(normalizar(nombre)));
    }

    @Override
    public synchronized Categoria addCategoria(Categoria categoria) throws DatoDuplicadoException {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser null");
        String claveNombre = normalizar(categoria.getNombre());
        if (indicePorNombreNormalizado.containsKey(claveNombre)) {
            throw new DatoDuplicadoException("Ya existe una categoría con ese nombre");
        }
        int id = categoria.getId();
        if (id <= 0) {
            id = secuenciaId.getAndIncrement();
            categoria.setId(id);
        } else {
            if (indicePorId.containsKey(id)) {
                throw new DatoDuplicadoException("Ya existe una categoría con id " + id);
            }
            final int idFinal = id;
            secuenciaId.updateAndGet(actual -> Math.max(actual, idFinal + 1));
        }
        categorias.add(categoria);
        indicePorId.put(id, categoria);
        indicePorNombreNormalizado.put(claveNombre, categoria);
        sincronizarLibrosDeCategoria(categoria);
        return categoria;
    }

    @Override
    public synchronized void updateCategoria(Categoria categoria) throws ElementoNoEncontradoException, DatoDuplicadoException {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser null");
        Categoria existente = indicePorId.get(categoria.getId());
        if (existente == null) {
            throw new ElementoNoEncontradoException("No existe la categoría con id " + categoria.getId());
        }
        String nuevoNombreNormalizado = normalizar(categoria.getNombre());
        Categoria conflictoNombre = indicePorNombreNormalizado.get(nuevoNombreNormalizado);
        if (conflictoNombre != null && conflictoNombre.getId() != categoria.getId()) {
            throw new DatoDuplicadoException("Ya existe otra categoría con ese nombre");
        }

        // actualizar colecciones de apoyo
        indicePorNombreNormalizado.remove(normalizar(existente.getNombre()));
        existente.setNombre(categoria.getNombre());
        indicePorNombreNormalizado.put(nuevoNombreNormalizado, existente);
        sincronizarLibrosDeCategoria(existente);
    }

    @Override
    public synchronized void deleteCategoria(int id) throws ElementoNoEncontradoException {
        Categoria categoria = indicePorId.remove(id);
        if (categoria == null) {
            throw new ElementoNoEncontradoException("No existe la categoría con id " + id);
        }
        categorias.remove(categoria);
        indicePorNombreNormalizado.remove(normalizar(categoria.getNombre()));
        for (Libro libro : categoria.getLibros()) {
            indiceLibros.remove(normalizarIsbn(libro.getIsbn()));
        }
    }

    @Override
    public synchronized List<Libro> findLibrosByCategoria(int categoriaId) throws ElementoNoEncontradoException {
        Categoria categoria = indicePorId.get(categoriaId);
        if (categoria == null) {
            throw new ElementoNoEncontradoException("Categoría no encontrada");
        }
        return new ArrayList<>(categoria.getLibros());
    }

    @Override
    public synchronized void addLibro(int categoriaId, Libro libro) throws DatoDuplicadoException, ElementoNoEncontradoException {
        Categoria categoria = obtenerCategoriaObligatoria(categoriaId);
        if (libro == null) throw new IllegalArgumentException("El libro no puede ser null");
        String claveIsbn = normalizarIsbn(libro.getIsbn());
        if (indiceLibros.containsKey(claveIsbn)) {
            throw new DatoDuplicadoException("Ya existe un libro con ISBN " + libro.getIsbn());
        }
        libro.setCategoriaId(categoriaId);
        libro.setCategoria(categoria.getNombre());
        categoria.agregarLibro(libro);
        indiceLibros.put(claveIsbn, libro);
    }

    @Override
    public synchronized void updateLibro(int categoriaId, Libro libro) throws ElementoNoEncontradoException {
        Categoria categoria = obtenerCategoriaObligatoria(categoriaId);
        String claveIsbn = normalizarIsbn(libro.getIsbn());
        Libro existente = indiceLibros.get(claveIsbn);
        if (existente == null) {
            throw new ElementoNoEncontradoException("No existe libro con ISBN " + libro.getIsbn());
        }
        if (!categoria.getLibros().contains(existente)) {
            throw new ElementoNoEncontradoException("El libro no pertenece a la categoría indicada");
        }
        existente.setTitulo(libro.getTitulo());
        existente.setAutor(libro.getAutor());
        existente.setCategoria(categoria.getNombre());
        existente.setCategoriaId(categoriaId);
        existente.setPrecio(libro.getPrecio());
        existente.setStock(libro.getStock());
    }

    @Override
    public synchronized void deleteLibro(int categoriaId, String isbn) throws ElementoNoEncontradoException {
        Categoria categoria = obtenerCategoriaObligatoria(categoriaId);
        if (!categoria.eliminarLibroPorIsbn(isbn)) {
            throw new ElementoNoEncontradoException("No existe libro con ISBN " + isbn + " en la categoría");
        }
        indiceLibros.remove(normalizarIsbn(isbn));
    }

    @Override
    public synchronized List<Libro> findAllLibros() {
        List<Libro> resultado = new ArrayList<>();
        for (Categoria categoria : categorias) {
            resultado.addAll(categoria.getLibros());
        }
        return resultado;
    }

    @Override
    public synchronized Optional<Libro> findLibroByIsbn(String isbn) {
        if (isbn == null) return Optional.empty();
        return Optional.ofNullable(indiceLibros.get(normalizarIsbn(isbn)));
    }

    @Override
    public synchronized List<Libro> filterLibrosByPrecio(double minimo, double maximo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : indiceLibros.values()) {
            double precio = libro.getPrecio();
            if (precio >= minimo && precio <= maximo) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Libro> filterLibrosByCategoria(String categoriaBuscada) {
        String filtro = categoriaBuscada == null ? "" : categoriaBuscada.trim().toLowerCase(Locale.ROOT);
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : indiceLibros.values()) {
            String categoria = libro.getCategoria() == null ? "" : libro.getCategoria().toLowerCase(Locale.ROOT);
            if (categoria.contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Libro> filterLibrosByAutor(String autorBuscado) {
        String filtro = autorBuscado == null ? "" : autorBuscado.trim().toLowerCase(Locale.ROOT);
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : indiceLibros.values()) {
            String autor = libro.getAutor() == null ? "" : libro.getAutor().toLowerCase(Locale.ROOT);
            if (autor.contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Libro> filterLibrosByStock(int stockMinimo, int stockMaximo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : indiceLibros.values()) {
            int stock = libro.getStock();
            if (stock >= stockMinimo && stock <= stockMaximo) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public void load() throws PersistenciaException {
        // implementación en memoria: no hace nada
    }

    @Override
    public void save() throws PersistenciaException {
        // implementación en memoria: no hace nada
    }

    protected Categoria obtenerCategoriaObligatoria(int categoriaId) throws ElementoNoEncontradoException {
        Categoria categoria = indicePorId.get(categoriaId);
        if (categoria == null) {
            throw new ElementoNoEncontradoException("Categoría no encontrada");
        }
        return categoria;
    }

    protected void sincronizarLibrosDeCategoria(Categoria categoria) {
        if (categoria == null) return;
        for (Libro libro : categoria.getLibros()) {
            libro.setCategoriaId(categoria.getId());
            libro.setCategoria(categoria.getNombre());
            indiceLibros.put(normalizarIsbn(libro.getIsbn()), libro);
        }
    }

    protected static String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
    }

    protected static String normalizarIsbn(String isbn) {
        return isbn == null ? "" : isbn.trim().toUpperCase(Locale.ROOT);
    }

    protected synchronized void limpiarTodo() {
        categorias.clear();
        indicePorId.clear();
        indicePorNombreNormalizado.clear();
        indiceLibros.clear();
        secuenciaId.set(1);
    }
}
