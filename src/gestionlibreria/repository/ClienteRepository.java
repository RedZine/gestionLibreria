package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    List<Cliente> findAll();
    Optional<Cliente> findById(int id);
    Optional<Cliente> findByNombreApellido(String nombre, String apellido);
    
    void add(Cliente cliente) throws DatoDuplicadoException;
    void update(Cliente cliente) throws ElementoNoEncontradoException;
    void deleteById(int id) throws ElementoNoEncontradoException;
    
    List<Cliente> filterByNombre(String nombre);
    
    void load() throws PersistenciaException;
    void save() throws PersistenciaException;
}