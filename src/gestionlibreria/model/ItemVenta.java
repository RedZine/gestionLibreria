package gestionlibreria.model;

public class ItemVenta {
    private String isbn;
    private int cantidad;
    private double precioUnitario;

    public ItemVenta() {}

    public ItemVenta(String isbn, int cantidad, double precioUnitario) {
        this.isbn = isbn;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
