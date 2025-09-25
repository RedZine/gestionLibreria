package gestionlibreria.repository;

import gestionlibreria.model.Cliente;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de gestionar a los clientes.
 */
public interface ClienteRepository {
    void cargar() throws IOException;

    void guardar() throws IOException;

    List<Cliente> obtenerTodos();

    Optional<Cliente> buscarPorId(String idCliente);

    void agregar(Cliente cliente) throws DatoDuplicadoException;

    void actualizar(Cliente cliente) throws DatoNoEncontradoException;
}
