package gestionlibreria.model;

import java.util.Objects;

/**
 * Representa un libro disponible en el catálogo de la librería.
 */
public class Libro {
    private String titulo;
    private String isbn;
    private String autor;
    private String categoria;
    private String idioma;
    private int anioPublicacion;
    private int paginas;
    private String editorial;
    private int edicion;
    private double precio;
    private int stock;

    /**
     * Construye un libro con toda la información relevante para la venta.
     */
    public Libro(String titulo, String isbn, String autor, String categoria, String idioma,
                 int anioPublicacion, int paginas, String editorial, int edicion,
                 double precio, int stock) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.autor = autor;
        this.categoria = categoria;
        this.idioma = idioma;
        this.anioPublicacion = anioPublicacion;
        this.paginas = paginas;
        this.editorial = editorial;
        this.edicion = edicion;
        this.precio = precio;
        this.stock = stock;
    }

    /**
     * Devuelve una representación amigable del libro para listados.
     */
    @Override
    public String toString() {
        return titulo + " (" + isbn + ") - " + autor + " | " + categoria + " | $" + precio +
                " | stock: " + stock;
    }

    /**
     * Dos libros son iguales si comparten el mismo ISBN.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Libro otro = (Libro) obj;
        return Objects.equals(isbn, otro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getEdicion() {
        return edicion;
    }

    public void setEdicion(int edicion) {
        this.edicion = edicion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Ajusta el stock considerando un movimiento de inventario.
     */
    public void ajustarStock(int cantidad) {
        this.stock += cantidad;
    }

    /**
     * Calcula el valor potencial del inventario del libro.
     */
    public double calcularValorInventario() {
        return precio * stock;
    }
}
