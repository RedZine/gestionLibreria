package gestionlibreria.model;

/**
 * Representa un libro incluido en una venta con su cantidad y precio.
 */
public class ItemVenta {
    private Libro libro;
    private int cantidad;
    private double precioUnitario;

    /**
     * Crea un ítem asociado a un libro y su precio.
     */
    public ItemVenta(Libro libro, int cantidad, double precioUnitario) {
        this.libro = libro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Calcula el subtotal que aporta el ítem a la venta.
     */
    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
}
