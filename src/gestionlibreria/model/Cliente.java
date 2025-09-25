package gestionlibreria.model;

/**
 * Representa a un cliente que compra en la librería.
 */
public class Cliente {
    private String idCliente;
    private String nombreCliente;
    private String correo;
    private String direccion;

    /**
     * Crea un cliente identificable para las ventas.
     */
    public Cliente(String idCliente, String nombreCliente, String correo, String direccion) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correo = correo;
        this.direccion = direccion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Entrega un resumen textual del cliente.
     */
    @Override
    public String toString() {
        return nombreCliente + " (" + idCliente + ") - " + correo;
    }
}
