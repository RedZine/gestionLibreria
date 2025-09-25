package gestionlibreria.repository;

import gestionlibreria.model.Venta;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de persistir las ventas.
 */
public interface VentaRepository {
    void cargar() throws IOException;

    void guardar() throws IOException;

    List<Venta> obtenerTodas();

    Optional<Venta> buscarPorId(String idVenta);

    void agregar(Venta venta) throws DatoDuplicadoException;

    void actualizar(Venta venta) throws DatoNoEncontradoException;

    void eliminar(String idVenta) throws DatoNoEncontradoException;
}
