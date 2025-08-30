package gestionlibreria.util;

import gestionlibreria.model.Categoria;
import gestionlibreria.repository.CategoriaRepository;

public class DataSeeder {
    public static void seed(CategoriaRepository repo){
        // Categorías base
        repo.agregarCategoria(new Categoria(1,"Ficción"));
        repo.agregarCategoria(new Categoria(2,"Tecnología"));
        repo.agregarCategoria(new Categoria(3,"Infantil"));

        // Libros iniciales
        repo.agregarLibro(1, "9780001","Duna","Frank Herbert","SciFi",15990,8);
        repo.agregarLibro(1, "9780002","Neuromante","William Gibson","SciFi",13990,6);
        repo.agregarLibro(2, "9781001","Clean Code","Robert C. Martin","Programación",32990,5);
        repo.agregarLibro(2, "9781002","Estructuras de Datos","Weiss","Programación",28990,4);
        repo.agregarLibro(3, "9782001","El Principito","Saint-Exupéry","Cuento",9990,12);
    }
}
