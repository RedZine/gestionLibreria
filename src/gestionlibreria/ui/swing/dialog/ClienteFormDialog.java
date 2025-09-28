package gestionlibreria.ui.swing.dialog;

import gestionlibreria.model.Cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteFormDialog extends JDialog {
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtApellido = new JTextField(20);
    private boolean confirmado = false;
    private Cliente cliente;

    public ClienteFormDialog(Window owner, Cliente cliente, boolean modoEdicion) {
        super(owner, modoEdicion ? "Editar cliente" : "Nuevo cliente", ModalityType.APPLICATION_MODAL);
        this.cliente = (cliente == null) ? new Cliente() : cliente;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; form.add(txtNombre, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1; form.add(txtApellido, gbc);

        if (cliente.getNombre() != null) txtNombre.setText(cliente.getNombre());
        if (cliente.getApellido() != null) txtApellido.setText(cliente.getApellido());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> onGuardar());
        btnCancelar.addActionListener(e -> { confirmado = false; dispose(); });
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);
    }

    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El apellido es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() { return confirmado; }
    public Cliente getCliente() { return cliente; }
}
