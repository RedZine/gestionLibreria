package gestionlibreria.ui.swing.model;

import gestionlibreria.model.Venta;
import gestionlibreria.repository.ClienteRepository;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentaTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"ID", "Fecha", "Cliente", "Medio Pago", "Items", "Total"};

    private final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    private List<Venta> ventas = new ArrayList<>();
    private ClienteRepository clienteRepository; // Para obtener nombres de clientes

    public VentaTableModel() { }

    public VentaTableModel(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = (ventas != null) ? new ArrayList<>(ventas) : new ArrayList<>();
        fireTableDataChanged();
    }

    public List<Venta> getVentas() {
        return new ArrayList<>(ventas);
    }

    public Venta getVentaAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < ventas.size()) {
            return ventas.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return ventas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Integer.class; // ID
            case 1: return String.class;  // Fecha (formateada)
            case 2: return String.class;  // Cliente
            case 3: return String.class;  // Medio Pago
            case 4: return Integer.class; // Items
            case 5: return Double.class;  // Total
            default: return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= ventas.size()) return null;

        Venta venta = ventas.get(rowIndex);
        switch (columnIndex) {
            case 0: return venta.getId();
            case 1: return formatearFecha(venta.getFecha());
            case 2: return obtenerNombreCliente(venta.getIdCliente());
            case 3: return venta.getMedioPago();
            case 4: return (venta.getItems() == null) ? 0 : venta.getItems().size();
            case 5: return venta.getSubtotal();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Tabla de solo lectura
    }

    private String obtenerNombreCliente(int idCliente) {
        if (clienteRepository != null) {
            return clienteRepository.findById(idCliente)
                    .map(c -> (c.getNombre() + " " + c.getApellido()).trim())
                    .orElse("Cliente #" + idCliente);
        }
        return "Cliente #" + idCliente;
    }

    private String formatearFecha(Object fecha) {
        if (fecha == null) return "";
        if (fecha instanceof Date) return SDF.format((Date) fecha);
        // si ya viene como String u otro tipo, lo mostramos tal cual
        return String.valueOf(fecha);
    }

    public void setClienteRepository(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
}
