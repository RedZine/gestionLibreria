package gestionlibreria.repository;

import gestionlibreria.model.Cliente;
import java.util.ArrayList;

public class ClienteRepository {
    private final ArrayList<Cliente> listaClientes = new ArrayList<Cliente>();

    public void agregar(Cliente cliente) { listaClientes.add(cliente); }

    public ArrayList<Cliente> listar() { return listaClientes; }

    public Cliente buscarPorId(int idCliente) {
        for (Cliente cliente : listaClientes) {
            if (cliente.getId() == idCliente) return cliente;
        }
        return null;
    }
}
