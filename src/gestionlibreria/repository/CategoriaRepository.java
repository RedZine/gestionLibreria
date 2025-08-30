package gestionlibreria.repository;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriaRepository {
    private final ArrayList<Categoria> categorias = new ArrayList<>();           // 1ª colección
    private final Map<String, Libro> indiceIsbn = new HashMap<>();               // Map ISBN -> Libro
    private final Map<String, ArrayList<Libro>> porCategoria = new HashMap<>();  // Map categoria -> libros

    // ---- Categorías ----
    public void agregarCategoria(Categoria c){ categorias.add(c); }
    public ArrayList<Categoria> listarCategorias(){ return categorias; }
    public Categoria buscarPorId(int id){
        for (Categoria c : categorias) if (c.getId()==id) return c;
        return null;
    }

    // ---- Libros (2ª colección anidada) ----
    // Sobrecarga SIA1.6
    public boolean agregarLibro(int idCategoria, Libro l){
        Categoria c = buscarPorId(idCategoria);
        if (c==null || l==null) return false;
        c.getLibros().add(l);
        actualizarIndices(l);
        return true;
    }
    public boolean agregarLibro(int idCategoria, String isbn, String titulo, String autor,
                                String categoria, double precio, int stock){
        return agregarLibro(idCategoria, new Libro(isbn,titulo,autor,categoria,precio,stock));
    }

    public ArrayList<Libro> listarLibrosDeCategoria(int idCategoria){
        Categoria c = buscarPorId(idCategoria);
        return (c==null) ? new ArrayList<>() : c.getLibros();
    }

    public Libro buscarLibroPorIsbn(String isbn){ return indiceIsbn.get(isbn); }

    public ArrayList<Libro> sugerenciasPorCategoria(String categoria, String excluirIsbn){
        ArrayList<Libro> base = porCategoria.getOrDefault(categoria, new ArrayList<>());
        ArrayList<Libro> out = new ArrayList<>();
        for (Libro l : base) if (!l.getIsbn().equals(excluirIsbn)) out.add(l);
        return out;
    }

    private void actualizarIndices(Libro l){
        indiceIsbn.put(l.getIsbn(), l);
        porCategoria.computeIfAbsent(l.getCategoria(), k -> new ArrayList<>()).add(l);
    }
}
