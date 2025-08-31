package gestionlibreria.app;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.util.DataSeeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final CategoriaRepository repo = new CategoriaRepository();

    public static void main(String[] args) throws IOException {
        DataSeeder.seed(repo);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String op;
        do {
            System.out.println("\n=== Gestión Librería ===");
            System.out.println("1) Insertar LIBRO en una CATEGORÍA");
            System.out.println("2) Mostrar LIBROS de una CATEGORÍA");
            System.out.println("3) Mostrar CATEGORÍAS");
            System.out.println("4) Sugerencias por categoría (relacionados)");
            System.out.println("0) Salir");
            System.out.print("> ");
            op = in.readLine();

            switch (op) {
                case "1":
                    insertarLibro(in);
                    break;
                case "2":
                    listarLibros(in);
                    break;
                case "3":
                    listarCategorias();
                    break;
                case "4":
                    sugerencias(in);
                    break;
                default:
                    // nada
            }
        } while(!"0".equals(op));
    }

    private static void listarCategorias(){
        System.out.println("\n-- Categorías --");
        for (Categoria c : repo.listarCategorias()) {
            System.out.println("[" + c.getId() + "] " + c.getNombre()
                    + " (libros: " + c.getLibros().size() + ")");
        }
    }

    private static void insertarLibro(BufferedReader in) throws IOException {
        listarCategorias();
        System.out.print("ID Categoría: "); int id = leerInt(in);
        System.out.print("ISBN: "); String isbn = in.readLine();
        System.out.print("Título: "); String titulo = in.readLine();
        System.out.print("Autor: "); String autor = in.readLine();
        System.out.print("Categoría textual: "); String categoria = in.readLine();
        System.out.print("Precio: "); double precio = leerDouble(in);
        System.out.print("Stock: "); int stock = leerInt(in);

        boolean ok = repo.agregarLibro(id, isbn, titulo, autor, categoria, precio, stock);
        System.out.println(ok ? "✅ Libro agregado." : "⚠️ Categoría no encontrada.");
    }

    private static void listarLibros(BufferedReader in) throws IOException {
        listarCategorias();
        System.out.print("ID Categoría: "); int id = leerInt(in);
        ArrayList<Libro> libros = repo.listarLibrosDeCategoria(id);
        if (libros.isEmpty()){
            System.out.println("Sin libros o categoría inexistente.");
            return;
        }
        System.out.println("\n-- Libros --");
        for (Libro l : libros) {
            System.out.println(l.getIsbn()+" | "+l.getTitulo()+" | "+l.getAutor()
                    +" | "+l.getCategoria()+" | $"+l.getPrecio()+" | stock "+l.getStock());
        }
    }

    private static void sugerencias(BufferedReader in) throws IOException {
        System.out.print("ISBN base: "); String isbn = in.readLine();
        Libro base = repo.buscarLibroPorIsbn(isbn);
        if (base == null){
            System.out.println("No existe ese ISBN.");
            return;
        }
        var sug = repo.sugerenciasPorCategoria(base.getCategoria(), base.getIsbn());
        System.out.println("\n-- Relacionados ("+base.getCategoria()+") --");
        if (sug.isEmpty()){
            System.out.println("Sin sugerencias.");
            return;
        }
        for (Libro l : sug) {
            System.out.println(l.getIsbn()+" | "+l.getTitulo()+" ("+l.getAutor()+")");
        }
    }

    private static int leerInt(BufferedReader in) throws IOException {
        try { return Integer.parseInt(in.readLine()); }
        catch(Exception e){ return 0; }
    }

    private static double leerDouble(BufferedReader in) throws IOException {
        try { return Double.parseDouble(in.readLine()); }
        catch(Exception e){ return 0.0; }
    }
}
