package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Venta;

import java.util.List;
import java.util.Optional;

public interface VentaRepository {
    List<Venta> findAll();
    Optional<Venta> findById(int id);
    
    void add(Venta venta) throws DatoDuplicadoException;
    void update(Venta venta) throws ElementoNoEncontradoException;
    void deleteById(int id) throws ElementoNoEncontradoException;
    
    // Filtros específicos para ventas
    List<Venta> filterByFechaRange(String fechaDesde, String fechaHasta);
    List<Venta> filterByCliente(String nombreCliente);
    List<Venta> filterByMedioPago(String medioPago);
    
    // Reportes
    double getTotalVentasPorPeriodo(String fechaDesde, String fechaHasta);
    
    void load() throws PersistenciaException;
    void save() throws PersistenciaException;
}