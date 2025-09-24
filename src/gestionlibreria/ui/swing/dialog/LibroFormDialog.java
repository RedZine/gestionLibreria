package gestionlibreria.ui.swing.dialog;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Libro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LibroFormDialog extends JDialog {
    private final JTextField txtIsbn = new JTextField(15);
    private final JTextField txtTitulo = new JTextField(25);
    private final JTextField txtAutor = new JTextField(25);
    private final JTextField txtPrecio = new JTextField(10);
    private final JTextField txtStock = new JTextField(5);
    private final JComboBox<Categoria> cboCategoria;
    private boolean confirmado = false;
    private final Libro libro;
    private final boolean permitirCambiarCategoria;

    public LibroFormDialog(Window owner, List<Categoria> categorias, Libro libro, boolean modoEdicion, boolean permitirCambiarCategoria) {
        super(owner, modoEdicion ? "Editar libro" : "Nuevo libro", ModalityType.APPLICATION_MODAL);
        this.libro = libro;
        this.permitirCambiarCategoria = permitirCambiarCategoria;
        this.cboCategoria = new JComboBox<>(categorias.toArray(new Categoria[0]));
        construirFormulario(modoEdicion);
        cargarDatos(libro, modoEdicion);
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirFormulario(boolean modoEdicion) {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; form.add(txtIsbn, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; form.add(txtTitulo, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; form.add(txtAutor, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; form.add(cboCategoria, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1; form.add(txtPrecio, gbc);

        fila++;
        gbc.gridx = 0; gbc.gridy = fila; form.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1; form.add(txtStock, gbc);

        add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        botones.add(btnAceptar);
        botones.add(btnCancelar);
        add(botones, BorderLayout.SOUTH);

        txtIsbn.setEditable(!modoEdicion);
        cboCategoria.setEnabled(permitirCambiarCategoria);

        btnAceptar.addActionListener(e -> onAceptar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDatos(Libro libro, boolean modoEdicion) {
        txtIsbn.setText(libro.getIsbn());
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        if (libro.getPrecio() != 0) {
            txtPrecio.setText(String.valueOf(libro.getPrecio()));
        }
        if (libro.getStock() != 0) {
            txtStock.setText(String.valueOf(libro.getStock()));
        }
        if (libro.getCategoriaId() > 0) {
            for (int i = 0; i < cboCategoria.getItemCount(); i++) {
                if (cboCategoria.getItemAt(i).getId() == libro.getCategoriaId()) {
                    cboCategoria.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void onAceptar() {
        try {
            String isbn = txtIsbn.getText().trim();
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            Categoria categoria = (Categoria) cboCategoria.getSelectedItem();

            if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || categoria == null) {
                throw new IllegalArgumentException("Todos los campos son obligatorios");
            }

            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setCategoriaId(categoria.getId());
            libro.setCategoria(categoria.getNombre());
            libro.setPrecio(precio);
            libro.setStock(stock);

            confirmado = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser números válidos", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Libro getLibro() {
        return libro;
    }
}
