package gestionlibreria.repository;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import java.util.ArrayList;
import java.util.Hashtable;

public class CategoriaRepository {
    private final ArrayList<Categoria> listaCategorias = new ArrayList<Categoria>();
    private final Hashtable<String, Libro> indiceLibrosPorIsbn = new Hashtable<String, Libro>();
    private final Hashtable<String, ArrayList<Libro>> indiceLibrosPorCategoria = new Hashtable<String, ArrayList<Libro>>();

    public void agregarCategoria(Categoria categoria) {
        listaCategorias.add(categoria);
    }

    public ArrayList<Categoria> listarCategorias() {
        return listaCategorias;
    }

    public Categoria buscarCategoriaPorId(int idCategoria) {
        for (Categoria categoria : listaCategorias) {
            if (categoria.getId() == idCategoria) {
                return categoria;
            }
        }
        return null;
    }

    public boolean agregarLibro(int idCategoria, Libro libro) {
        Categoria categoria = buscarCategoriaPorId(idCategoria);
        if (categoria == null || libro == null) return false;

        categoria.getLibros().add(libro);
        actualizarIndices(libro);
        return true;
    }

    public boolean agregarLibro(int idCategoria, String isbn, String titulo, String autor,
                                String categoriaTexto, double precio, int stock) {
        Libro libro = new Libro(isbn, titulo, autor, categoriaTexto, precio, stock);
        return agregarLibro(idCategoria, libro);
    }

    public ArrayList<Libro> listarLibrosDeCategoria(int idCategoria) {
        Categoria categoria = buscarCategoriaPorId(idCategoria);
        if (categoria == null) {
            return new ArrayList<Libro>();
        }
        return categoria.getLibros();
    }

    public Libro buscarLibroPorIsbn(String isbn) {
        return indiceLibrosPorIsbn.get(isbn);
    }

    public ArrayList<Libro> sugerenciasPorCategoria(String nombreCategoria, String isbnExcluir) {
        ArrayList<Libro> lista = indiceLibrosPorCategoria.get(nombreCategoria);
        ArrayList<Libro> resultado = new ArrayList<Libro>();
        if (lista == null) return resultado;

        for (Libro libro : lista) {
            if (!libro.getIsbn().equals(isbnExcluir)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public boolean disminuirStock(String isbn, int cantidad) {
        Libro libro = indiceLibrosPorIsbn.get(isbn);
        if (libro == null) return false;
        if (libro.getStock() < cantidad) return false;
        libro.setStock(libro.getStock() - cantidad);
        return true;
    }

    private void actualizarIndices(Libro libro) {
        indiceLibrosPorIsbn.put(libro.getIsbn(), libro);

        String categoria = libro.getCategoria();
        ArrayList<Libro> listaPorCat = indiceLibrosPorCategoria.get(categoria);
        if (listaPorCat == null) {
            listaPorCat = new ArrayList<Libro>();
            indiceLibrosPorCategoria.put(categoria, listaPorCat);
        }
        listaPorCat.add(libro);
    }
}
