
package gestionlibreria.repository;

import gestionlibreria.model.Libro;
import gestionlibreria.util.DatoObligatorioException;
import gestionlibreria.util.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de libros en memoria.
 * Cumple validaciones de negocio y lanza excepciones específicas.
 */
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

    /** Valida campos obligatorios y reglas simples del negocio. */
    private void validar(Libro libro) {
        if (libro == null) throw new DatoObligatorioException("El libro es obligatorio.");
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty())
            throw new DatoObligatorioException("El ISBN es obligatorio.");
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty())
            throw new DatoObligatorioException("El título es obligatorio.");
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty())
            throw new DatoObligatorioException("El autor es obligatorio.");
        if (libro.getPrecio() < 0)
            throw new DatoObligatorioException("El precio no puede ser negativo.");
        if (libro.getStock() < 0)
            throw new DatoObligatorioException("El stock no puede ser negativo.");
    }

    @Override
    public void add(Libro libro) {
        validar(libro);
        if (findByIsbn(libro.getIsbn()).isPresent())
            throw new DatoObligatorioException("ISBN duplicado: " + libro.getIsbn());
        datos.add(libro);
    }

    @Override
    public void update(Libro libroActualizado) {
        validar(libroActualizado);
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getIsbn().equalsIgnoreCase(libroActualizado.getIsbn())) {
                datos.set(i, libroActualizado);
                return;
            }
        }
        throw new EntidadNoEncontradaException("No existe el ISBN: " + libroActualizado.getIsbn());
    }

    @Override
    public void deleteByIsbn(String isbn) {
        boolean eliminado = datos.removeIf(l -> l.getIsbn().equalsIgnoreCase(isbn));
        if (!eliminado) {
            throw new EntidadNoEncontradaException("No existe el ISBN: " + isbn);
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
public List<Libro> filterByStock(int minimo, int maximo) {
    int desde = Math.min(minimo, maximo);
    int hasta = Math.max(minimo, maximo);

    List<Libro> resultado = new ArrayList<>();
    for (Libro l : datos) {
        int s = l.getStock();
        if (s >= desde && s <= hasta) {
            resultado.add(l);
        }
    }
    return resultado;
}


    @Override public void load() { /* no-op en memoria */ }
    @Override public void save() { /* no-op en memoria */ }


    
}
