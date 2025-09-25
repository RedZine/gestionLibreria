package gestionlibreria.repository;

import gestionlibreria.config.AppConfig;
import gestionlibreria.model.Promocion;
import gestionlibreria.model.enums.AlcancePromo;
import gestionlibreria.model.enums.TipoPromo;
import gestionlibreria.util.ArchivoUtil;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación persistente para las promociones.
 */
public class ArchivoPromocionRepository implements PromocionRepository {
    private final List<Promocion> promociones = new ArrayList<>();

    @Override
    public void cargar() throws IOException {
        promociones.clear();
        List<String> lineas = ArchivoUtil.leerLineas(AppConfig.RUTA_PROMOCIONES);
        for (String linea : lineas) {
            if (linea.isBlank()) {
                continue;
            }
            String[] datos = linea.split(";");
            if (datos.length < 5) {
                System.out.println("Línea de promoción inválida en CSV: " + linea);
                continue;
            }
            try {
                Promocion promocion = new Promocion(datos[0], datos[1], TipoPromo.valueOf(datos[2]), AlcancePromo.valueOf(datos[3]), Double.parseDouble(datos[4]));
                promociones.add(promocion);
            } catch (IllegalArgumentException ex) {
                System.out.println("No se pudo cargar la promoción: " + linea);
            }
        }
    }

    @Override
    public void guardar() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Promocion promocion : promociones) {
            String linea = String.join(";",
                    promocion.getId(),
                    promocion.getDescripcion(),
                    promocion.getTipo().name(),
                    promocion.getAlcance().name(),
                    String.valueOf(promocion.getValor())
            );
            lineas.add(linea);
        }
        ArchivoUtil.escribirLineas(AppConfig.RUTA_PROMOCIONES, lineas);
    }

    @Override
    public List<Promocion> obtenerTodas() {
        return Collections.unmodifiableList(promociones);
    }

    @Override
    public Optional<Promocion> buscarPorId(String id) {
        return promociones.stream().filter(promocion -> promocion.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public void agregar(Promocion promocion) throws DatoDuplicadoException {
        if (buscarPorId(promocion.getId()).isPresent()) {
            throw new DatoDuplicadoException("Ya existe una promoción con ID " + promocion.getId());
        }
        promociones.add(promocion);
    }

    @Override
    public void eliminar(String id) throws DatoNoEncontradoException {
        Optional<Promocion> existente = buscarPorId(id);
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró la promoción con ID " + id);
        }
        promociones.remove(existente.get());
    }
}
