package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryClienteRepository implements ClienteRepository {
    private final List<Cliente> clientes = new ArrayList<>();
    private final AtomicInteger secuenciaId = new AtomicInteger(1);

    public InMemoryClienteRepository() {
        // Agregar algunos clientes de prueba
        try {
            add(new Cliente(0, "Juan", "Pérez", "123456789", "juan@email.com", "Calle 123"));
            add(new Cliente(0, "María", "González", "987654321", "maria@email.com", "Avenida 456"));
            add(new Cliente(0, "Carlos", "López", "555666777", "carlos@email.com", "Plaza Central"));
        } catch (DatoDuplicadoException e) {
            // Ignorar errores en datos de prueba
        }
    }

    @Override
    public List<Cliente> findAll() {
        return new ArrayList<>(clientes);
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return clientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<Cliente> findByNombreApellido(String nombre, String apellido) {
        if (nombre == null || apellido == null) {
            return Optional.empty();
        }
        return clientes.stream()
                .filter(c -> nombre.equalsIgnoreCase(c.getNombre()) && 
                           apellido.equalsIgnoreCase(c.getApellido()))
                .findFirst();
    }

    @Override
    public void add(Cliente cliente) throws DatoDuplicadoException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        
        // Validar duplicados por nombre y apellido
        if (findByNombreApellido(cliente.getNombre(), cliente.getApellido()).isPresent()) {
            throw new DatoDuplicadoException("Ya existe un cliente con ese nombre y apellido");
        }
        
        // Asignar ID si no lo tiene
        if (cliente.getId() <= 0) {
            cliente.setId(secuenciaId.getAndIncrement());
        } else {
            // Verificar que el ID no esté duplicado
            if (findById(cliente.getId()).isPresent()) {
                throw new DatoDuplicadoException("Ya existe un cliente con ID " + cliente.getId());
            }
            secuenciaId.updateAndGet(actual -> Math.max(actual, cliente.getId() + 1));
        }
        
        clientes.add(cliente);
    }

    @Override
    public void update(Cliente cliente) throws ElementoNoEncontradoException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                return;
            }
        }
        throw new ElementoNoEncontradoException("No existe cliente con ID " + cliente.getId());
    }

    @Override
    public void deleteById(int id) throws ElementoNoEncontradoException {
        boolean eliminado = clientes.removeIf(c -> c.getId() == id);
        if (!eliminado) {
            throw new ElementoNoEncontradoException("No existe cliente con ID " + id);
        }
    }

    @Override
    public List<Cliente> filterByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>(clientes);
        }
        
        String filtro = nombre.trim().toLowerCase();
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente cliente : clientes) {
            String nombreCompleto = (cliente.getNombre() + " " + cliente.getApellido()).toLowerCase();
            if (nombreCompleto.contains(filtro)) {
                resultado.add(cliente);
            }
        }
        return resultado;
    }

    @Override
    public void load() throws PersistenciaException {
        // Implementación en memoria: no hace nada
    }

    @Override
    public void save() throws PersistenciaException {
        // Implementación en memoria: no hace nada
    }
}