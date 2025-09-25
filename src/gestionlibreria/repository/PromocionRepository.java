package gestionlibreria.repository;

import gestionlibreria.model.Promocion;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para almacenar las promociones configuradas.
 */
public interface PromocionRepository {
    void cargar() throws IOException;

    void guardar() throws IOException;

    List<Promocion> obtenerTodas();

    Optional<Promocion> buscarPorId(String id);

    void agregar(Promocion promocion) throws DatoDuplicadoException;

    void eliminar(String id) throws DatoNoEncontradoException;
}
