package gestionlibreria.ui.swing;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;
import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.ui.swing.dialog.CategoriaFormDialog;
import gestionlibreria.ui.swing.dialog.LibroFormDialog;
import gestionlibreria.ui.swing.model.CategoriaListModel;
import gestionlibreria.ui.swing.model.LibroTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CategoriesPanel extends JPanel {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaListModel categoriaListModel = new CategoriaListModel();
    private final LibroTableModel librosTableModel = new LibroTableModel();
    private final JList<Categoria> listaCategorias = new JList<>(categoriaListModel);
    private final JTable tablaLibros = new JTable(librosTableModel);

    public CategoriesPanel(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
        inicializarComponentes();
        refrescarCategorias();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.3);

        JPanel panelCategorias = construirPanelCategorias();
        JPanel panelLibros = construirPanelLibros();

        splitPane.setLeftComponent(panelCategorias);
        splitPane.setRightComponent(panelLibros);

        add(splitPane, BorderLayout.CENTER);

        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarLibrosDeSeleccion();
            }
        });
    }

    private JPanel construirPanelCategorias() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Categorías"));
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(listaCategorias), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnRefrescar);
        panel.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarCategoria());
        btnEditar.addActionListener(e -> editarCategoria());
        btnEliminar.addActionListener(e -> eliminarCategoria());
        btnRefrescar.addActionListener(e -> refrescarCategorias());

        return panel;
    }

    private JPanel construirPanelLibros() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Libros de la categoría"));
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar libro");
        JButton btnEditar = new JButton("Editar libro");
        JButton btnEliminar = new JButton("Eliminar libro");
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarLibroCategoria());
        btnEditar.addActionListener(e -> editarLibroCategoria());
        btnEliminar.addActionListener(e -> eliminarLibroCategoria());

        return panel;
    }

    private void refrescarCategorias() {
        List<Categoria> categorias = categoriaRepository.findAllCategorias();
        categoriaListModel.setCategorias(categorias);
        if (!categorias.isEmpty()) {
            listaCategorias.setSelectedIndex(0);
        } else {
            librosTableModel.setLibros(java.util.Collections.emptyList());
        }
    }

    private void cargarLibrosDeSeleccion() {
        Categoria categoria = listaCategorias.getSelectedValue();
        if (categoria == null) {
            librosTableModel.setLibros(java.util.Collections.emptyList());
            return;
        }
        try {
            librosTableModel.setLibros(categoriaRepository.findLibrosByCategoria(categoria.getId()));
        } catch (ElementoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarCategoria() {
        Categoria categoria = new Categoria();
        CategoriaFormDialog dialog = new CategoriaFormDialog(SwingUtilities.getWindowAncestor(this), categoria);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                categoriaRepository.addCategoria(categoria);
                refrescarCategorias();
            } catch (DatoDuplicadoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCategoria() {
        Categoria seleccionada = listaCategorias.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Categoria copia = new Categoria(seleccionada.getId(), seleccionada.getNombre());
        CategoriaFormDialog dialog = new CategoriaFormDialog(SwingUtilities.getWindowAncestor(this), copia);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                categoriaRepository.updateCategoria(copia);
                refrescarCategorias();
            } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCategoria() {
        Categoria seleccionada = listaCategorias.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría " + seleccionada.getNombre() + " y sus libros?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoriaRepository.deleteCategoria(seleccionada.getId());
                refrescarCategorias();
            } catch (ElementoNoEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarLibroCategoria() {
        Categoria categoria = listaCategorias.getSelectedValue();
        if (categoria == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Libro libro = new Libro();
        libro.setCategoriaId(categoria.getId());
        libro.setCategoria(categoria.getNombre());
        LibroFormDialog dialog = new LibroFormDialog(SwingUtilities.getWindowAncestor(this), categoriaRepository.findAllCategorias(), libro, false, false);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                categoriaRepository.addLibro(categoria.getId(), dialog.getLibro());
                cargarLibrosDeSeleccion();
            } catch (DatoDuplicadoException | ElementoNoEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarLibroCategoria() {
        Categoria categoria = listaCategorias.getSelectedValue();
        Libro seleccionado = obtenerLibroSeleccionado();
        if (categoria == null || seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría y un libro", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Libro copia = new Libro(seleccionado.getIsbn(), seleccionado.getTitulo(), seleccionado.getAutor(), seleccionado.getCategoria(), seleccionado.getPrecio(), seleccionado.getStock(), seleccionado.getCategoriaId());
        LibroFormDialog dialog = new LibroFormDialog(SwingUtilities.getWindowAncestor(this), categoriaRepository.findAllCategorias(), copia, true, false);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            try {
                categoriaRepository.updateLibro(categoria.getId(), dialog.getLibro());
                cargarLibrosDeSeleccion();
            } catch (ElementoNoEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarLibroCategoria() {
        Categoria categoria = listaCategorias.getSelectedValue();
        Libro libro = obtenerLibroSeleccionado();
        if (categoria == null || libro == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría y un libro", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro " + libro.getTitulo() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoriaRepository.deleteLibro(categoria.getId(), libro.getIsbn());
                cargarLibrosDeSeleccion();
            } catch (ElementoNoEncontradoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Libro obtenerLibroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int modeloIndex = tablaLibros.convertRowIndexToModel(fila);
        return librosTableModel.getLibroAt(modeloIndex);
    }
}
