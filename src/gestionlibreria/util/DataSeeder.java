package gestionlibreria.util;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Cliente;
import gestionlibreria.model.Promocion;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.PromocionRepository;

public class DataSeeder {

    public static void seed(CategoriaRepository categoriaRepositorio,
                            ClienteRepository clienteRepositorio,
                            PromocionRepository promocionRepositorio) {

        categoriaRepositorio.agregarCategoria(new Categoria(1, "Ficcion"));
        categoriaRepositorio.agregarCategoria(new Categoria(2, "Tecnologia"));
        categoriaRepositorio.agregarCategoria(new Categoria(3, "Infantil"));

        categoriaRepositorio.agregarLibro(1, "9780001","Duna","Frank Herbert","SciFi",15990,8);
        categoriaRepositorio.agregarLibro(1, "9780002","Neuromante","William Gibson","SciFi",13990,6);
        categoriaRepositorio.agregarLibro(2, "9781001","Clean Code","Robert C. Martin","Programacion",32990,5);
        categoriaRepositorio.agregarLibro(2, "9781002","Estructuras de Datos","Weiss","Programacion",28990,4);
        categoriaRepositorio.agregarLibro(3, "9782001","El Principito","Saint-Exupery","Cuento",9990,12);

        clienteRepositorio.agregar(new Cliente(1, "Ana Perez", "ana@mail.com"));
        clienteRepositorio.agregar(new Cliente(2, "Juan Soto", "juan@mail.com"));

        promocionRepositorio.agregar(new Promocion(1, "PORCENTAJE", "GENERAL", null, 10, true));
        promocionRepositorio.agregar(new Promocion(2, "PORCENTAJE", "CATEGORIA", "Programacion", 15, true));
        promocionRepositorio.agregar(new Promocion(3, "FIJO", "CATEGORIA", "SciFi", 3000, false));
    }
}
