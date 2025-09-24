package gestionlibreria.ui.swing.dialog;

import gestionlibreria.model.Categoria;

import javax.swing.*;
import java.awt.*;

public class CategoriaFormDialog extends JDialog {
    private final JTextField txtNombre = new JTextField(20);
    private boolean confirmado = false;

    public CategoriaFormDialog(Window owner, Categoria categoria) {
        super(owner, categoria.getId() > 0 ? "Editar categoría" : "Nueva categoría", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre.setText(categoria.getNombre());
        txtNombre.setColumns(20);
        form.add(txtNombre, gbc);

        add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        botones.add(btnAceptar);
        botones.add(btnCancelar);
        add(botones, BorderLayout.SOUTH);

        btnAceptar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            categoria.setNombre(txtNombre.getText().trim());
            confirmado = true;
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
