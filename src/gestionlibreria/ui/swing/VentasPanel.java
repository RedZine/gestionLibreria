package gestionlibreria.ui.swing;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Cliente;
import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Venta;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.repository.VentaRepository;
import gestionlibreria.ui.swing.dialog.VentaFormDialog;
import gestionlibreria.ui.swing.model.VentaTableModel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentasPanel extends JPanel {
    private final VentaRepository ventaRepository;
    private final LibroRepository libroRepository;
    private final ClienteRepository clienteRepository;
    private final VentaTableModel tableModel;
    private final JTable tablaVentas;

    private final JTextField txtFechaDesde = new JTextField(8);
    private final JTextField txtFechaHasta = new JTextField(8);
    private final JTextField txtBuscarCliente = new JTextField(10);
    private final JComboBox<String> cmbMedioPago = new JComboBox<>(new String[]{"TODOS", "EFECTIVO", "TARJETA"});
    private final JLabel lblTotalVentas = new JLabel("Total: $0.00");

    public VentasPanel(VentaRepository ventaRepository, LibroRepository libroRepository, ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.libroRepository = libroRepository;
        this.clienteRepository = clienteRepository;
        this.tableModel = new VentaTableModel(clienteRepository);
        this.tablaVentas = new JTable(tableModel);
        inicializarComponentes();
        cargarVentas();
        actualizarTotalVentas();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Configurar tabla
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVentas.setAutoCreateRowSorter(true);

        // Ajustar anchos de columnas
        TableColumnModel columnModel = tablaVentas.getColumnModel();
        if (columnModel.getColumnCount() > 0) {
            columnModel.getColumn(0).setPreferredWidth(50);  // ID
            columnModel.getColumn(1).setPreferredWidth(100); // Fecha
            columnModel.getColumn(2).setPreferredWidth(150); // Cliente
            columnModel.getColumn(3).setPreferredWidth(80);  // Medio Pago
            columnModel.getColumn(4).setPreferredWidth(80);  // Items
            columnModel.getColumn(5).setPreferredWidth(100); // Total
        }

        add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

        JPanel panelFiltros = construirPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(construirPanelBotones(), BorderLayout.EAST);
        panelSur.add(construirPanelTotal(), BorderLayout.WEST);
        add(panelSur, BorderLayout.SOUTH);

        // Configurar formato de fecha por defecto
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date hoy = new Date();
        txtFechaHasta.setText(sdf.format(hoy));

        // Fecha desde: hace 30 días
        Date hace30Dias = new Date(hoy.getTime() - (30L * 24 * 60 * 60 * 1000));
        txtFechaDesde.setText(sdf.format(hace30Dias));
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;
        // Filtros por fecha
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(new JLabel("Fecha desde (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; panel.add(txtFechaDesde, gbc);
        gbc.gridx = 2; panel.add(new JLabel("hasta:"), gbc);
        gbc.gridx = 3; panel.add(txtFechaHasta, gbc);
        gbc.gridx = 4;
        JButton btnFiltrarFecha = new JButton("Filtrar por fecha");
        btnFiltrarFecha.addActionListener(e -> filtrarPorFecha());
        panel.add(btnFiltrarFecha, gbc);

        fila++;
        // Filtros por cliente y medio de pago
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(new JLabel("Cliente contiene:"), gbc);
        gbc.gridx = 1; panel.add(txtBuscarCliente, gbc);
        gbc.gridx = 2;
        JButton btnFiltrarCliente = new JButton("Buscar cliente");
        btnFiltrarCliente.addActionListener(e -> filtrarPorCliente());
        panel.add(btnFiltrarCliente, gbc);

        gbc.gridx = 3; panel.add(new JLabel("Medio de pago:"), gbc);
        gbc.gridx = 4; panel.add(cmbMedioPago, gbc);
        gbc.gridx = 5;
        JButton btnFiltrarMedio = new JButton("Filtrar medio");
        btnFiltrarMedio.addActionListener(e -> filtrarPorMedioPago());
        panel.add(btnFiltrarMedio, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila;
        JButton btnLimpiar = new JButton("Ver todas");
        btnLimpiar.addActionListener(e -> {
            cargarVentas();
            actualizarTotalVentas();
        });
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevaVenta = new JButton("Nueva Venta");
        JButton btnVerDetalle = new JButton("Ver Detalle");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnNuevaVenta.addActionListener(e -> crearNuevaVenta());
        btnVerDetalle.addActionListener(e -> verDetalleVenta());
        btnEliminar.addActionListener(e -> eliminarVenta());
        btnRefrescar.addActionListener(e -> {
            cargarVentas();
            actualizarTotalVentas();
        });

        panel.add(btnNuevaVenta);
        panel.add(btnVerDetalle);
        panel.add(btnEliminar);
        panel.add(btnRefrescar);
        return panel;
    }

    private JPanel construirPanelTotal() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalVentas.setFont(lblTotalVentas.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblTotalVentas);
        return panel;
    }

    private void cargarVentas() {
        tableModel.setVentas(ventaRepository.findAll());
    }

    private void actualizarTotalVentas() {
        List<Venta> ventasActuales = tableModel.getVentas();
        double total = 0.0;
        for (Venta venta : ventasActuales) {
            total += venta.getSubtotal();
        }
        lblTotalVentas.setText(String.format("Total mostrado: $%.2f", total));
    }

    private void crearNuevaVenta() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Libro> librosDisponibles = new ArrayList<>();
        for (Libro libro : libroRepository.findAll()) {
            if (libro.getStock() > 0) {
                librosDisponibles.add(libro);
            }
        }

        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe crear clientes antes de registrar ventas",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (librosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay libros disponibles para vender",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Venta nuevaVenta = new Venta();
        VentaFormDialog dialog = new VentaFormDialog(
                SwingUtilities.getWindowAncestor(this),
                clienteRepository,          // <<--- agregado
                clientes,
                librosDisponibles,
                nuevaVenta,
                false
        );
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            try {
                // Actualizar stock de libros vendidos
                for (ItemVenta item : dialog.getVenta().getItems()) {
                    Libro libro = libroRepository.findByIsbn(item.getIsbn()).orElse(null);
                    if (libro != null) {
                        libro.setStock(libro.getStock() - item.getCantidad());
                        libroRepository.update(libro);
                    }
                }

                ventaRepository.add(dialog.getVenta());
                cargarVentas();
                actualizarTotalVentas();
                JOptionPane.showMessageDialog(this, "Venta registrada exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (DatoDuplicadoException | ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetalleVenta() {
        Venta seleccionada = obtenerVentaSeleccionada();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una venta",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Crear copia para evitar modificaciones accidentales
        Venta copia = new Venta(seleccionada.getId(), seleccionada.getIdCliente(),
                seleccionada.getFecha(), seleccionada.getMedioPago());
        copia.setItems(new ArrayList<>(seleccionada.getItems()));

        VentaFormDialog dialog = new VentaFormDialog(
                SwingUtilities.getWindowAncestor(this),
                clienteRepository,          // <<--- agregado
                clienteRepository.findAll(),
                libroRepository.findAll(),
                copia,
                true  // Solo lectura
        );
        dialog.setVisible(true);
    }

    private void eliminarVenta() {
        Venta seleccionada = obtenerVentaSeleccionada();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta primero",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("¿Seguro que desea eliminar la venta #%d?\n\nEsto restaurará el stock de los libros vendidos.",
                        seleccionada.getId()),
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Restaurar stock
                for (ItemVenta item : seleccionada.getItems()) {
                    Libro libro = libroRepository.findByIsbn(item.getIsbn()).orElse(null);
                    if (libro != null) {
                        libro.setStock(libro.getStock() + item.getCantidad());
                        libroRepository.update(libro);
                    }
                }

                ventaRepository.deleteById(seleccionada.getId());
                cargarVentas();
                actualizarTotalVentas();
                JOptionPane.showMessageDialog(this, "Venta eliminada exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Venta obtenerVentaSeleccionada() {
        int fila = tablaVentas.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modeloIndex = tablaVentas.convertRowIndexToModel(fila);
        return tableModel.getVentaAt(modeloIndex);
    }

    private void filtrarPorFecha() {
        String fechaDesde = txtFechaDesde.getText().trim();
        String fechaHasta = txtFechaHasta.getText().trim();

        if (fechaDesde.isEmpty() || fechaHasta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese ambas fechas en formato dd/MM/yyyy",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Venta> ventasFiltradas = ventaRepository.filterByFechaRange(fechaDesde, fechaHasta);
        tableModel.setVentas(ventasFiltradas);
        actualizarTotalVentas();
    }

    private void filtrarPorCliente() {
        String cliente = txtBuscarCliente.getText().trim();
        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese texto para buscar cliente",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Venta> ventasFiltradas = ventaRepository.filterByCliente(cliente);
        tableModel.setVentas(ventasFiltradas);
        actualizarTotalVentas();
    }

    private void filtrarPorMedioPago() {
        String medio = (String) cmbMedioPago.getSelectedItem();
        if ("TODOS".equals(medio)) {
            cargarVentas();
        } else {
            List<Venta> ventasFiltradas = ventaRepository.filterByMedioPago(medio);
            tableModel.setVentas(ventasFiltradas);
        }
        actualizarTotalVentas();
    }
}
