package gestionlibreria.repository;

import gestionlibreria.config.AppConfig;
import gestionlibreria.model.Cliente;
import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Venta;
import gestionlibreria.model.enums.MedioPago;
import gestionlibreria.util.ArchivoUtil;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementación que persiste ventas en un archivo CSV.
 */
public class ArchivoVentaRepository implements VentaRepository {
    private final List<Venta> ventas = new ArrayList<>();
    private final CatalogoRepository catalogoRepository;
    private final ClienteRepository clienteRepository;

    public ArchivoVentaRepository(CatalogoRepository catalogoRepository, ClienteRepository clienteRepository) {
        this.catalogoRepository = catalogoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void cargar() throws IOException {
        ventas.clear();
        List<String> lineas = ArchivoUtil.leerLineas(AppConfig.RUTA_VENTAS);
        for (String linea : lineas) {
            if (linea.isBlank()) {
                continue;
            }
            String[] datos = linea.split(";");
            if (datos.length < 5) {
                System.out.println("Línea de venta inválida en CSV: " + linea);
                continue;
            }
            Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(datos[1]);
            if (clienteOpt.isEmpty()) {
                System.out.println("Cliente no encontrado para venta: " + linea);
                continue;
            }
            try {
                Venta venta = new Venta(datos[0], clienteOpt.get(), LocalDate.parse(datos[2]), MedioPago.valueOf(datos[3]));
                String[] items = datos[4].split("\\|");
                for (String itemCadena : items) {
                    if (itemCadena.isBlank()) {
                        continue;
                    }
                    String[] camposItem = itemCadena.split("#");
                    Optional<Libro> libroOpt = catalogoRepository.buscarPorIsbn(camposItem[0]);
                    if (libroOpt.isEmpty()) {
                        System.out.println("Libro no encontrado para ítem: " + itemCadena);
                        continue;
                    }
                    int cantidad = Integer.parseInt(camposItem[1]);
                    double precio = Double.parseDouble(camposItem[2]);
                    venta.agregarItem(new ItemVenta(libroOpt.get(), cantidad, precio));
                }
                ventas.add(venta);
            } catch (Exception ex) {
                System.out.println("No se pudo cargar la venta: " + linea);
            }
        }
    }

    @Override
    public void guardar() throws IOException {
        List<String> lineas = new ArrayList<>();
        for (Venta venta : ventas) {
            List<String> items = new ArrayList<>();
            venta.getItems().forEach(item -> items.add(item.getLibro().getIsbn() + "#" + item.getCantidad() + "#" + item.getPrecioUnitario()));
            String linea = String.join(";",
                    venta.getIdVenta(),
                    venta.getCliente().getIdCliente(),
                    venta.getFecha().toString(),
                    venta.getMedioPago().name(),
                    String.join("|", items)
            );
            lineas.add(linea);
        }
        ArchivoUtil.escribirLineas(AppConfig.RUTA_VENTAS, lineas);
    }

    @Override
    public List<Venta> obtenerTodas() {
        return Collections.unmodifiableList(ventas);
    }

    @Override
    public Optional<Venta> buscarPorId(String idVenta) {
        return ventas.stream().filter(venta -> venta.getIdVenta().equalsIgnoreCase(idVenta)).findFirst();
    }

    @Override
    public void agregar(Venta venta) throws DatoDuplicadoException {
        if (buscarPorId(venta.getIdVenta()).isPresent()) {
            throw new DatoDuplicadoException("Ya existe una venta con ID " + venta.getIdVenta());
        }
        ventas.add(venta);
    }

    @Override
    public void actualizar(Venta venta) throws DatoNoEncontradoException {
        Optional<Venta> existente = buscarPorId(venta.getIdVenta());
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró la venta con ID " + venta.getIdVenta());
        }
        Venta original = existente.get();
        List<ItemVenta> nuevosItems = new ArrayList<>();
        venta.getItems().forEach(item -> nuevosItems.add(new ItemVenta(item.getLibro(), item.getCantidad(), item.getPrecioUnitario())));
        original.setCliente(venta.getCliente());
        original.setFecha(venta.getFecha());
        original.setMedioPago(venta.getMedioPago());
        original.limpiarItems();
        nuevosItems.forEach(original::agregarItem);
    }

    @Override
    public void eliminar(String idVenta) throws DatoNoEncontradoException {
        Optional<Venta> existente = buscarPorId(idVenta);
        if (existente.isEmpty()) {
            throw new DatoNoEncontradoException("No se encontró la venta con ID " + idVenta);
        }
        ventas.remove(existente.get());
    }
}
