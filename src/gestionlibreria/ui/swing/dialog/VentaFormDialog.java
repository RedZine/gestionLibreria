package gestionlibreria.ui.swing.dialog;

import gestionlibreria.model.Cliente;
import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Venta;
import gestionlibreria.repository.ClienteRepository;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentaFormDialog extends JDialog {
    private final ClienteRepository clienteRepository;   // repo para guardar el nuevo cliente
    private final List<Cliente> clientes;                // lista de clientes disponible
    private final List<Libro> libros;                    // lista de libros disponible
    private final boolean soloLectura;

    private Venta venta;
    private boolean confirmado = false;

    // Componentes de la interfaz
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<String> cmbMedioPago;
    private JTextField txtFecha;
    private JTable tablaItems;
    private ItemVentaTableModel modeloTabla;
    private JLabel lblTotal;

    // Para agregar items
    private JComboBox<Libro> cmbLibro;
    private JSpinner spnCantidad;
    private JTextField txtPrecio;

    public VentaFormDialog(Window parent,
                           ClienteRepository clienteRepository,
                           List<Cliente> clientes,
                           List<Libro> libros,
                           Venta venta,
                           boolean soloLectura) {
        super(parent, soloLectura ? "Detalle de Venta" : "Nueva Venta", ModalityType.APPLICATION_MODAL);
        this.clienteRepository = clienteRepository;
        this.clientes = clientes;
        this.libros = libros;
        this.venta = venta;
        this.soloLectura = soloLectura;

        inicializarComponentes();
        cargarDatos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Panel superior - datos generales
        JPanel panelSuperior = crearPanelDatosGenerales();
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central - items de venta
        JPanel panelCentral = crearPanelItems();
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior - botones
        JPanel panelInferior = crearPanelBotones();
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelDatosGenerales() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos Generales"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        cmbCliente = new JComboBox<>();
        for (Cliente c : clientes) cmbCliente.addItem(c);
        cmbCliente.setRenderer(new ClienteRenderer());
        cmbCliente.setEnabled(!soloLectura);

        // contenedor combo + botón "Nuevo"
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCliente.add(cmbCliente);
        if (!soloLectura) {
            panelCliente.add(Box.createHorizontalStrut(6));
            JButton btnNuevoCliente = new JButton("Nuevo");
            btnNuevoCliente.addActionListener(e -> onNuevoCliente());
            panelCliente.add(btnNuevoCliente);
        }
        panel.add(panelCliente, gbc);

        // Fecha
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx = 3;
        txtFecha = new JTextField(10);
        txtFecha.setEditable(!soloLectura);
        panel.add(txtFecha, gbc);

        // Medio de pago
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Medio de Pago:"), gbc);
        gbc.gridx = 1;
        cmbMedioPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
        cmbMedioPago.setEnabled(!soloLectura);
        panel.add(cmbMedioPago, gbc);

        // Total
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 3;
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        panel.add(lblTotal, gbc);

        return panel;
    }

    private JPanel crearPanelItems() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Items de Venta"));

        // Tabla de items
        modeloTabla = new ItemVentaTableModel();
        tablaItems = new JTable(modeloTabla);
        tablaItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaItems);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar items (solo si no es solo lectura)
        if (!soloLectura) {
            JPanel panelAgregar = crearPanelAgregarItem();
            panel.add(panelAgregar, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel crearPanelAgregarItem() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Libro:"));
        cmbLibro = new JComboBox<>();
        for (Libro libro : libros) cmbLibro.addItem(libro);
        cmbLibro.setRenderer(new LibroRenderer());
        cmbLibro.addActionListener(e -> actualizarPrecioLibro());
        panel.add(cmbLibro);

        panel.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        panel.add(spnCantidad);

        panel.add(new JLabel("Precio:"));
        txtPrecio = new JTextField(8);
        panel.add(txtPrecio);

        JButton btnAgregar = new JButton("Agregar Item");
        btnAgregar.addActionListener(e -> agregarItem());
        panel.add(btnAgregar);

        JButton btnQuitar = new JButton("Quitar Item");
        btnQuitar.addActionListener(e -> quitarItem());
        panel.add(btnQuitar);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (!soloLectura) {
            JButton btnGuardar = new JButton("Guardar");
            btnGuardar.addActionListener(e -> guardarVenta());
            panel.add(btnGuardar);
        }

        JButton btnCancelar = new JButton(soloLectura ? "Cerrar" : "Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);

        return panel;
    }

    private void cargarDatos() {
        if (venta.getFecha() != null && !venta.getFecha().isEmpty()) {
            txtFecha.setText(venta.getFecha());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtFecha.setText(sdf.format(new Date()));
        }

        if (venta.getIdCliente() > 0) {
            for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                if (cmbCliente.getItemAt(i).getId() == venta.getIdCliente()) {
                    cmbCliente.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (venta.getMedioPago() != null) {
            cmbMedioPago.setSelectedItem(venta.getMedioPago());
        }

        modeloTabla.setItems(venta.getItems());
        actualizarTotal();

        if (!soloLectura && cmbLibro.getItemCount() > 0) {
            actualizarPrecioLibro();
        }
    }

    private void actualizarPrecioLibro() {
        Libro libroSeleccionado = (Libro) cmbLibro.getSelectedItem();
        if (libroSeleccionado != null) {
            txtPrecio.setText(String.valueOf(libroSeleccionado.getPrecio()));
        }
    }

    private void agregarItem() {
        try {
            Libro libro = (Libro) cmbLibro.getSelectedItem();
            int cantidad = (Integer) spnCantidad.getValue();
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            if (libro == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un libro", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cantidad > libro.getStock()) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + libro.getStock(),
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verificar si ya existe el item
            for (ItemVenta item : venta.getItems()) {
                if (item.getIsbn().equals(libro.getIsbn())) {
                    JOptionPane.showMessageDialog(this, "El libro ya está en la venta. Modifique la cantidad del item existente.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            ItemVenta nuevoItem = new ItemVenta(libro.getIsbn(), cantidad, precio);
            venta.agregarItem(nuevoItem);
            modeloTabla.setItems(venta.getItems());
            actualizarTotal();

            spnCantidad.setValue(1);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio válido", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void quitarItem() {
        int filaSeleccionada = tablaItems.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un item para quitar", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        venta.getItems().remove(filaSeleccionada);
        modeloTabla.setItems(venta.getItems());
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = venta.getSubtotal();
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void guardarVenta() {
        try {
            Cliente clienteSeleccionado = (Cliente) cmbCliente.getSelectedItem();
            if (clienteSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String fecha = txtFecha.getText().trim();
            if (fecha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la fecha", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (venta.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe agregar al menos un item a la venta", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            venta.setIdCliente(clienteSeleccionado.getId());
            venta.setFecha(fecha);
            venta.setMedioPago((String) cmbMedioPago.getSelectedItem());

            confirmado = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === Nuevo: crear cliente desde el diálogo ===
    private void onNuevoCliente() {
        ClienteFormDialog dlg = new ClienteFormDialog(SwingUtilities.getWindowAncestor(this), null, false);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            try {
                Cliente nuevo = dlg.getCliente();
                clienteRepository.add(nuevo);  // persiste y asigna ID
                clientes.add(nuevo);           // mantenemos la lista fuente
                cmbCliente.addItem(nuevo);     // actualizamos el combo
                cmbCliente.setSelectedItem(nuevo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isConfirmado() { return confirmado; }
    public Venta getVenta() { return venta; }

    // Renderer para mostrar clientes en el combo
    private static class ClienteRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Cliente) {
                Cliente cliente = (Cliente) value;
                setText((cliente.getNombre() + " " + cliente.getApellido()).trim());
            }
            return this;
        }
    }

    // Renderer para mostrar libros en el combo
    private static class LibroRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Libro) {
                Libro libro = (Libro) value;
                setText(String.format("%s - %s (Stock: %d)", libro.getTitulo(), libro.getAutor(), libro.getStock()));
            }
            return this;
        }
    }

    // Modelo de tabla para los items de venta
    private class ItemVentaTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = {"ISBN", "Título", "Cantidad", "Precio Unit.", "Subtotal"};
        private List<ItemVenta> items = new ArrayList<>();

        public void setItems(List<ItemVenta> items) {
            this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return items.size(); }
        @Override public int getColumnCount() { return COLUMN_NAMES.length; }
        @Override public String getColumnName(int column) { return COLUMN_NAMES[column]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;  // ISBN
                case 1: return String.class;  // Título
                case 2: return Integer.class; // Cantidad
                case 3: return Double.class;  // Precio
                case 4: return Double.class;  // Subtotal
                default: return Object.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= items.size()) return null;

            ItemVenta item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.getIsbn();
                case 1: return obtenerTituloLibro(item.getIsbn());
                case 2: return item.getCantidad();
                case 3: return item.getPrecioUnitario();
                case 4: return item.getSubtotal();
                default: return null;
            }
        }

        private String obtenerTituloLibro(String isbn) {
            for (Libro libro : libros) {
                if (libro.getIsbn().equals(isbn)) return libro.getTitulo();
            }
            return "Libro no encontrado";
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
    }
}
