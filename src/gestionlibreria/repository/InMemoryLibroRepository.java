// src/gestionlibreria/repository/InMemoryLibroRepository.java
package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Libro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLibroRepository implements LibroRepository {
    private final List<Libro> datos = new ArrayList<>();

    @Override
    public List<Libro> findAll() {
        return new ArrayList<>(datos); // copia defensiva
    }

    @Override
    public Optional<Libro> findByIsbn(String isbn) {
        for (Libro libro : datos) {
            if (libro.getIsbn().equalsIgnoreCase(isbn)) {
                return Optional.of(libro);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(Libro libro) throws DatoDuplicadoException {
        if (findByIsbn(libro.getIsbn()).isPresent()) {
            throw new DatoDuplicadoException("ISBN duplicado: " + libro.getIsbn());
        }
        datos.add(libro);
    }

    @Override
    public void update(Libro libroActualizado) throws ElementoNoEncontradoException {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getIsbn().equalsIgnoreCase(libroActualizado.getIsbn())) {
                datos.set(i, libroActualizado);
                return;
            }
        }
        throw new ElementoNoEncontradoException("No existe el ISBN: " + libroActualizado.getIsbn());
    }

    @Override
    public void deleteByIsbn(String isbn) throws ElementoNoEncontradoException {
        boolean eliminado = datos.removeIf(l -> l.getIsbn().equalsIgnoreCase(isbn));
        if (!eliminado) {
            throw new ElementoNoEncontradoException("No existe el ISBN: " + isbn);
        }
    }

    @Override
    public List<Libro> filterByPrecio(double minimo, double maximo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : datos) {
            double precio = libro.getPrecio();
            if (precio >= minimo && precio <= maximo) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public List<Libro> filterByCategoria(String categoria) {
        String filtro = categoria == null ? "" : categoria.trim().toLowerCase();
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : datos) {
            String cat = libro.getCategoria() == null ? "" : libro.getCategoria().trim().toLowerCase();
            if (cat.contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public List<Libro> filterByAutor(String autorContiene) {
        String filtro = autorContiene == null ? "" : autorContiene.trim().toLowerCase();
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : datos) {
            String aut = libro.getAutor() == null ? "" : libro.getAutor().trim().toLowerCase();
            if (aut.contains(filtro)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override
    public List<Libro> filterByStock(int stockMinimo, int stockMaximo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : datos) {
            int stock = libro.getStock();
            if (stock >= stockMinimo && stock <= stockMaximo) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    @Override public void load() throws PersistenciaException { /* no-op en memoria */ }
    @Override public void save() throws PersistenciaException { /* no-op en memoria */ }
}
