// src/gestionlibreria/repository/LibroRepository.java
package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroRepository {
    List<Libro> findAll();
    Optional<Libro> findByIsbn(String isbn);

    // CRUD (SIA2.12)
    void add(Libro libro) throws DatoDuplicadoException, ElementoNoEncontradoException;
    void update(Libro libro) throws ElementoNoEncontradoException;
    void deleteByIsbn(String isbn) throws ElementoNoEncontradoException;

    // Filtros / búsquedas (SIA2.5 / SIA2.13)
    List<Libro> filterByPrecio(double minimo, double maximo);
    List<Libro> filterByCategoria(String categoria);   // coincidencia parcial
    List<Libro> filterByAutor(String autorContiene);   // coincidencia parcial
    List<Libro> filterByStock(int stockMinimo, int stockMaximo);

    // Persistencia batch (SIA2.2)
    void load() throws PersistenciaException;
    void save() throws PersistenciaException;
}
