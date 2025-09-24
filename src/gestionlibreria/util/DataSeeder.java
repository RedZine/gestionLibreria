package gestionlibreria.util;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;

public class DataSeeder {
    public static void seed(CategoriaRepository repo){
        // Categorías base
        try {
            Categoria ficcion = repo.addCategoria(new Categoria(1,"Ficción"));
            Categoria tecnologia = repo.addCategoria(new Categoria(2,"Tecnología"));
            Categoria infantil = repo.addCategoria(new Categoria(3,"Infantil"));

            // Libros iniciales
            repo.addLibro(ficcion.getId(), new Libro("9780001","Duna","Frank Herbert","Ciencia Ficción",15990,8));
            repo.addLibro(ficcion.getId(), new Libro("9780002","Neuromante","William Gibson","Ciencia Ficción",13990,6));
            repo.addLibro(tecnologia.getId(), new Libro("9781001","Clean Code","Robert C. Martin","Programación",32990,5));
            repo.addLibro(tecnologia.getId(), new Libro("9781002","Estructuras de Datos","Weiss","Programación",28990,4));
            repo.addLibro(infantil.getId(), new Libro("9782001","El Principito","Saint-Exupéry","Cuento",9990,12));
        } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
            // Si ya existen datos simplemente se ignoran, el objetivo es contar con una carga inicial.
        }
    }
}
