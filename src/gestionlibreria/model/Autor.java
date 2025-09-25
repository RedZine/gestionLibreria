package gestionlibreria.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa a un autor con su bibliografía disponible en la librería.
 */
public class Autor {
    private String nombre;
    private String id;
    private String pais;
    private final List<Libro> listaLibros;

    /**
     * Crea un autor y su lista inicial de libros.
     */
    public Autor(String nombre, String id, String pais) {
        this.nombre = nombre;
        this.id = id;
        this.pais = pais;
        this.listaLibros = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Agrega un libro a la bibliografía del autor.
     */
    public void agregarLibro(Libro libro) {
        listaLibros.add(libro);
    }

    /**
     * Permite consultar una lista inmutable de libros del autor.
     */
    public List<Libro> getListaLibros() {
        return Collections.unmodifiableList(listaLibros);
    }

    @Override
    public String toString() {
        return nombre + " - " + pais + " | Obras: " + listaLibros.size();
    }
}
