package gestionlibreria.model;

import java.util.ArrayList;

public class Categoria {
    private int id;
    private String nombre;
    private ArrayList<Libro> libros = new ArrayList<Libro>();

    public Categoria() {}

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public ArrayList<Libro> getLibros() { return libros; }

    public void agregarLibro(Libro libro) {
        libros.add(libro);
    }
}
