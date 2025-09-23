// src/gestionlibreria/repository/LibroRepository.java
package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroRepository {
    List<Libro> findAll();
    Optional<Libro> findByIsbn(String isbn);

    // CRUD (SIA2.12)
    void add(Libro libro);
    void update(Libro libro);
    void deleteByIsbn(String isbn);

    // Filtros / búsquedas (SIA2.5 / SIA2.13)
    List<Libro> filterByPrecio(double minimo, double maximo);
    List<Libro> filterByCategoria(String categoria);   // coincidencia parcial
    List<Libro> filterByAutor(String autorContiene);   // coincidencia parcial

    // Persistencia batch (SIA2.2) — en memoria no hacen nada
    void load();
    void save();
}
