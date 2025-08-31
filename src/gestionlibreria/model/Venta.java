package gestionlibreria.model;

import java.util.ArrayList;

public class Venta {
    private int id;
    private int idCliente;
    private String fecha;
    private String medioPago;
    private ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();

    public Venta() {}

    public Venta(int id, int idCliente, String fecha, String medioPago) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.medioPago = medioPago;
    }

    public void agregarItem(ItemVenta item) {
        items.add(item);
    }

    public void agregarItem(String isbn, int cantidad, double precioUnitario) {
        items.add(new ItemVenta(isbn, cantidad, precioUnitario));
    }

    public double getSubtotal() {
        double total = 0.0;
        for (ItemVenta it : items) {
            total += it.getSubtotal();
        }
        return total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getMedioPago() { return medioPago; }
    public void setMedioPago(String medioPago) { this.medioPago = medioPago; }

    public ArrayList<ItemVenta> getItems() { return items; }
    public void setItems(ArrayList<ItemVenta> items) { this.items = items; }
}

