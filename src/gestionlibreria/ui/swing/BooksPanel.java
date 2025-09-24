package gestionlibreria.ui.swing;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.LibroRepository;
import gestionlibreria.ui.swing.dialog.LibroFormDialog;
import gestionlibreria.ui.swing.model.LibroTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BooksPanel extends JPanel {
    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final LibroTableModel tableModel = new LibroTableModel();
    private final JTable tablaLibros = new JTable(tableModel);

    private final JTextField txtPrecioMin = new JTextField(6);
    private final JTextField txtPrecioMax = new JTextField(6);
    private final JTextField txtStockMin = new JTextField(4);
    private final JTextField txtStockMax = new JTextField(4);
    private final JTextField txtBuscarAutor = new JTextField(10);
    private final JTextField txtBuscarCategoria = new JTextField(10);
    private final JTextField txtBuscarIsbn = new JTextField(10);

    public BooksPanel(LibroRepository libroRepository, CategoriaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
        inicializarComponentes();
        cargarLibros();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setAutoCreateRowSorter(true);
        add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        JPanel panelFiltros = construirPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);

        JPanel panelBotones = construirPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel construirPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Precio min:"), gbc);
        gbc.gridx = 1; panel.add(txtPrecioMin, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Precio max:"), gbc);
        gbc.gridx = 3; panel.add(txtPrecioMax, gbc);
        gbc.gridx = 4;
        JButton btnFiltrarPrecio = new JButton("Filtrar precio");
        btnFiltrarPrecio.addActionListener(e -> filtrarPorPrecio());
        panel.add(btnFiltrarPrecio, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Stock min:"), gbc);
        gbc.gridx = 1; panel.add(txtStockMin, gbc);
        gbc.gridx = 2; panel.add(new JLabel("Stock max:"), gbc);
        gbc.gridx = 3; panel.add(txtStockMax, gbc);
        gbc.gridx = 4;
        JButton btnFiltrarStock = new JButton("Filtrar stock");
        btnFiltrarStock.addActionListener(e -> filtrarPorStock());
        panel.add(btnFiltrarStock, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("Autor contiene:"), gbc);
        gbc.gridx = 1; panel.add(txtBuscarAutor, gbc);
        gbc.gridx = 2;
        JButton btnFiltrarAutor = new JButton("Buscar autor");
        btnFiltrarAutor.addActionListener(e -> filtrarPorAutor());
        panel.add(btnFiltrarAutor, gbc);

        gbc.gridx = 3; panel.add(new JLabel("Categoría contiene:"), gbc);
        gbc.gridx = 4; panel.add(txtBuscarCategoria, gbc);
        gbc.gridx = 5;
        JButton btnFiltrarCategoria = new JButton("Buscar categoría");
        btnFiltrarCategoria.addActionListener(e -> filtrarPorCategoria());
        panel.add(btnFiltrarCategoria, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; panel.add(txtBuscarIsbn, gbc);
        gbc.gridx = 2;
        JButton btnBuscarIsbn = new JButton("Buscar ISBN");
        btnBuscarIsbn.addActionListener(e -> buscarPorIsbn());
        panel.add(btnBuscarIsbn, gbc);

        gbc.gridx = 3;
        JButton btnLimpiar = new JButton("Ver todo");
        btnLimpiar.addActionListener(e -> cargarLibros());
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnAgregar.addActionListener(e -> agregarLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnRefrescar.addActionListener(e -> cargarLibros());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnRefrescar);
        return panel;
    }

    private void cargarLibros() {
        tableModel.setLibros(libroRepository.findAll());
    }

    private void agregarLibro() {
        List<Categoria> categorias = categoriaRepository.findAllCategorias();
        if (categorias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe crear categorías antes de agregar libros", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Libro nuevo = new Libro();
        LibroFormDialog dialog = new LibroFormDialog(SwingUtilities.getWindowAncestor(this), categorias, nuevo, false, true);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                libroRepository.add(dialog.getLibro());
                cargarLibros();
            } catch (DatoDuplicadoException | ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarLibro() {
        Libro seleccionado = obtenerLibroSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un libro", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Libro copia = new Libro(seleccionado.getIsbn(), seleccionado.getTitulo(), seleccionado.getAutor(), seleccionado.getCategoria(), seleccionado.getPrecio(), seleccionado.getStock(), seleccionado.getCategoriaId());
        LibroFormDialog dialog = new LibroFormDialog(SwingUtilities.getWindowAncestor(this), categoriaRepository.findAllCategorias(), copia, true, true);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                libroRepository.update(dialog.getLibro());
                cargarLibros();
            } catch (ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarLibro() {
        Libro seleccionado = obtenerLibroSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro primero", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el libro " + seleccionado.getTitulo() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                libroRepository.deleteByIsbn(seleccionado.getIsbn());
                cargarLibros();
            } catch (ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Libro obtenerLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modeloIndex = tablaLibros.convertRowIndexToModel(fila);
        return tableModel.getLibroAt(modeloIndex);
    }

    private void filtrarPorPrecio() {
        try {
            double min = Double.parseDouble(txtPrecioMin.getText().trim());
            double max = Double.parseDouble(txtPrecioMax.getText().trim());
            tableModel.setLibros(libroRepository.filterByPrecio(min, max));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos para el precio", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void filtrarPorStock() {
        try {
            int min = Integer.parseInt(txtStockMin.getText().trim());
            int max = Integer.parseInt(txtStockMax.getText().trim());
            tableModel.setLibros(libroRepository.filterByStock(min, max));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos para el stock", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void filtrarPorAutor() {
        String autor = txtBuscarAutor.getText().trim();
        if (autor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese texto de autor", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.setLibros(libroRepository.filterByAutor(autor));
    }

    private void filtrarPorCategoria() {
        String categoria = txtBuscarCategoria.getText().trim();
        if (categoria.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese texto de categoría", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.setLibros(libroRepository.filterByCategoria(categoria));
    }

    private void buscarPorIsbn() {
        String isbn = txtBuscarIsbn.getText().trim();
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ISBN", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Libro libro = libroRepository.findByIsbn(isbn).orElse(null);
        if (libro == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el ISBN", "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            tableModel.setLibros(java.util.Collections.singletonList(libro));
        }
    }
}
