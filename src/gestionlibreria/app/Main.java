// src/gestionlibreria/app/Main.java
package gestionlibreria.app;

import gestionlibreria.repository.InMemoryLibroRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.ui.console.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        // Implementación en memoria. (Cuando quieras CSV, solo cambia esta línea)
        LibroRepository repositorio = new InMemoryLibroRepository();
        repositorio.load(); // no-op en memoria

        new MenuPrincipal(repositorio).iniciar();

        repositorio.save(); // no-op en memoria
    }
}
