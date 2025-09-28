package gestionlibreria.repository;

import gestionlibreria.exception.DatoDuplicadoException;
import gestionlibreria.exception.ElementoNoEncontradoException;
import gestionlibreria.exception.PersistenciaException;
import gestionlibreria.model.Cliente;
import gestionlibreria.model.Venta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryVentaRepository implements VentaRepository {
    private final List<Venta> ventas = new ArrayList<>();
    private final AtomicInteger secuenciaId = new AtomicInteger(1);
    private final ClienteRepository clienteRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public InMemoryVentaRepository(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Venta> findAll() {
        return new ArrayList<>(ventas);
    }

    @Override
    public Optional<Venta> findById(int id) {
        return ventas.stream()
                .filter(v -> v.getId() == id)
                .findFirst();
    }

    @Override
    public void add(Venta venta) throws DatoDuplicadoException {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser null");
        }
        
        // Asignar ID si no lo tiene
        if (venta.getId() <= 0) {
            venta.setId(secuenciaId.getAndIncrement());
        } else {
            // Verificar que el ID no esté duplicado
            if (findById(venta.getId()).isPresent()) {
                throw new DatoDuplicadoException("Ya existe una venta con ID " + venta.getId());
            }
            secuenciaId.updateAndGet(actual -> Math.max(actual, venta.getId() + 1));
        }
        
        ventas.add(venta);
    }

    @Override
    public void update(Venta venta) throws ElementoNoEncontradoException {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser null");
        }
        
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getId() == venta.getId()) {
                ventas.set(i, venta);
                return;
            }
        }
        throw new ElementoNoEncontradoException("No existe venta con ID " + venta.getId());
    }

    @Override
    public void deleteById(int id) throws ElementoNoEncontradoException {
        boolean eliminado = ventas.removeIf(v -> v.getId() == id);
        if (!eliminado) {
            throw new ElementoNoEncontradoException("No existe venta con ID " + id);
        }
    }

    @Override
    public List<Venta> filterByFechaRange(String fechaDesde, String fechaHasta) {
        List<Venta> resultado = new ArrayList<>();
        
        try {
            Date desde = dateFormat.parse(fechaDesde);
            Date hasta = dateFormat.parse(fechaHasta);
            
            for (Venta venta : ventas) {
                try {
                    Date fechaVenta = dateFormat.parse(venta.getFecha());
                    if (!fechaVenta.before(desde) && !fechaVenta.after(hasta)) {
                        resultado.add(venta);
                    }
                } catch (ParseException e) {
                    // Ignorar ventas con fechas inválidas
                }
            }
        } catch (ParseException e) {
            // Si las fechas son inválidas, retornar lista vacía
        }
        
        return resultado;
    }

    @Override
    public List<Venta> filterByCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            return new ArrayList<>(ventas);
        }
        
        String filtro = nombreCliente.trim().toLowerCase();
        List<Venta> resultado = new ArrayList<>();
        
        for (Venta venta : ventas) {
            Optional<Cliente> cliente = clienteRepository.findById(venta.getIdCliente());
            if (cliente.isPresent()) {
                String nombreCompleto = (cliente.get().getNombre() + " " + cliente.get().getApellido()).toLowerCase();
                if (nombreCompleto.contains(filtro)) {
                    resultado.add(venta);
                }
            }
        }
        
        return resultado;
    }

    @Override
    public List<Venta> filterByMedioPago(String medioPago) {
        if (medioPago == null || medioPago.trim().isEmpty()) {
            return new ArrayList<>(ventas);
        }
        
        List<Venta> resultado = new ArrayList<>();
        for (Venta venta : ventas) {
            if (medioPago.equalsIgnoreCase(venta.getMedioPago())) {
                resultado.add(venta);
            }
        }
        
        return resultado;
    }

    @Override
    public double getTotalVentasPorPeriodo(String fechaDesde, String fechaHasta) {
        List<Venta> ventasPeriodo = filterByFechaRange(fechaDesde, fechaHasta);
        double total = 0;
        for (Venta venta : ventasPeriodo) {
            total += venta.getSubtotal();
        }
        return total;
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