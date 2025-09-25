package gestionlibreria.repository;

import gestionlibreria.config.AppConfig;
import gestionlibreria.model.Cliente;
import gestionlibreria.util.ArchivoUtil;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en archivo CSV para la lista de clientes.
 */
public class ArchivoClienteRepository implements ClienteRepository {
    private final List<Cliente> clientes = new ArrayList<>();

    @Override
    public void cargar() throws IOException {
        clientes.clear();
        List<String> lineas = ArchivoUtil.leerLineas(AppConfig.RUTA_CLIENTES);
        for (String linea : lineas) {
            if (linea.isBlank()) {
                continue;
            }
            String[] datos = linea.split(";");
            if (datos.length < 4) {
                System.out.println("Línea de cliente inválida en CSV: " + linea);
                continue;
            }
            Cliente cliente = new Cliente(datos[0], datos[1], datos[2], datos[3]);
            clientes.add(cliente);
        }
    }

    @Override
    public void guardar() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Cliente cliente : clientes) {
            String linea = String.join(";",
                    cliente.getIdCliente(),
                    cliente.getNombreCliente(),
                    cliente.getCorreo(),
                    cliente.getDireccion()
            );
            lineas.add(linea);
        }
        ArchivoUtil.escribirLineas(AppConfig.RUTA_CLIENTES, lineas);
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return Collections.unmodifiableList(clientes);
    }

    @Override
    public Optional<Cliente> buscarPorId(String idCliente) {
        return clientes.stream().filter(cliente -> cliente.getIdCliente().equalsIgnoreCase(idCliente)).findFirst();
    }

    @Override
    public void agregar(Cliente cliente) throws DatoDuplicadoException {
        if (buscarPorId(cliente.getIdCliente()).isPresent()) {
            throw new DatoDuplicadoException("Ya existe un cliente con ID " + cliente.getIdCliente());
        }
        clientes.add(cliente);
    }

    @Override
    public void actualizar(Cliente cliente) throws DatoNoEncontradoException {
        Optional<Cliente> existente = buscarPorId(cliente.getIdCliente());
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró el cliente con ID " + cliente.getIdCliente());
        }
        Cliente original = existente.get();
        original.setNombreCliente(cliente.getNombreCliente());
        original.setCorreo(cliente.getCorreo());
        original.setDireccion(cliente.getDireccion());
    }
}
