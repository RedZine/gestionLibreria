package gestionlibreria.model;

import gestionlibreria.model.enums.MedioPago;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una venta realizada en la librería.
 */
public class Venta {
    private String idVenta;
    private Cliente cliente;
    private LocalDate fecha;
    private MedioPago medioPago;
    private final List<ItemVenta> items;

    /**
     * Construye una venta con cliente e ítems vacíos.
     */
    public Venta(String idVenta, Cliente cliente, LocalDate fecha, MedioPago medioPago) {
        this.idVenta = idVenta;
        this.cliente = cliente;
        this.fecha = fecha;
        this.medioPago = medioPago;
        this.items = new ArrayList<>();
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    /**
     * Agrega un nuevo libro a la venta.
     */
    public void agregarItem(ItemVenta item) {
        items.add(item);
    }

    /**
     * Modifica los datos del ítem indicado.
     */
    public void editarItem(int indice, int cantidad, double precioUnitario) {
        ItemVenta item = items.get(indice);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);
    }

    /**
     * Elimina un ítem específico por índice.
     */
    public void eliminarItem(int indice) {
        items.remove(indice);
    }

    /**
     * Limpia todos los ítems de la venta.
     */
    public void limpiarItems() {
        items.clear();
    }

    /**
     * Entrega los ítems de forma inmutable para visualización.
     */
    public List<ItemVenta> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Calcula el total de la venta sumando los subtotales.
     */
    public double calcularTotal() {
        return items.stream().mapToDouble(ItemVenta::calcularSubtotal).sum();
    }

    @Override
    public String toString() {
        return "Venta " + idVenta + " - " + cliente.getNombreCliente() + " - Total: $" + calcularTotal();
    }
}
