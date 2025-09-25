package gestionlibreria.ui.console;

import gestionlibreria.model.Cliente;
import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Venta;
import gestionlibreria.model.enums.MedioPago;
import gestionlibreria.repository.CatalogoRepository;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.VentaRepository;
import gestionlibreria.util.DatoDuplicadoException;
import gestionlibreria.util.DatoNoEncontradoException;
import gestionlibreria.util.EntradaConsolaUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

/**
 * Menú para crear, editar y eliminar ventas y sus ítems asociados.
 */
public class MenuVentas {
    private final VentaRepository ventaRepository;
    private final CatalogoRepository catalogoRepository;
    private final ClienteRepository clienteRepository;
    private final Scanner scanner;

    public MenuVentas(VentaRepository ventaRepository, CatalogoRepository catalogoRepository, ClienteRepository clienteRepository, Scanner scanner) {
        this.ventaRepository = ventaRepository;
        this.catalogoRepository = catalogoRepository;
        this.clienteRepository = clienteRepository;
        this.scanner = scanner;
    }

    /**
     * Muestra las opciones disponibles para administrar ventas.
     */
    public void mostrar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gestión de Ventas ---");
            System.out.println("1. Listar ventas");
            System.out.println("2. Registrar nueva venta");
            System.out.println("3. Administrar ítems de una venta");
            System.out.println("4. Eliminar venta");
            System.out.println("0. Volver");
            String opcion = EntradaConsolaUtil.leerTexto(scanner, "Seleccione opción: ");
            switch (opcion) {
                case "1":
                    listarVentas();
                    break;
                case "2":
                    registrarVenta();
                    break;
                case "3":
                    administrarItems();
                    break;
                case "4":
                    eliminarVenta();
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private void listarVentas() {
        List<Venta> ventas = ventaRepository.obtenerTodas();
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        ventas.forEach(venta -> {
            System.out.println(venta);
            venta.getItems().forEach(item -> System.out.println("   * " + item.getLibro().getTitulo() + " x" + item.getCantidad() + " = $" + item.calcularSubtotal()));
        });
    }

    private void registrarVenta() {
        System.out.println("\n--- Nueva Venta ---");
        String idVenta = UUID.randomUUID().toString();
        Cliente cliente = solicitarCliente();
        if (cliente == null) {
            return;
        }
        LocalDate fecha = LocalDate.now();
        MedioPago medioPago = solicitarMedioPago();
        Venta venta = new Venta(idVenta, cliente, fecha, medioPago);
        agregarItemsAVenta(venta);
        if (venta.getItems().isEmpty()) {
            System.out.println("No se registraron ítems. Venta cancelada.");
            return;
        }
        try {
            ventaRepository.agregar(venta);
            System.out.println("Venta registrada con ID: " + idVenta);
        } catch (DatoDuplicadoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Cliente solicitarCliente() {
        String idCliente = EntradaConsolaUtil.leerTexto(scanner, "ID del cliente: ");
        Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(idCliente);
        if (clienteOpt.isPresent()) {
            return clienteOpt.get();
        }
        System.out.println("Cliente no encontrado. ¿Desea registrarlo? (s/n)");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        if (!"s".equals(respuesta)) {
            return null;
        }
        String nombre = EntradaConsolaUtil.leerTexto(scanner, "Nombre: ");
        String correo = EntradaConsolaUtil.leerTexto(scanner, "Correo: ");
        String direccion = EntradaConsolaUtil.leerTexto(scanner, "Dirección: ");
        Cliente nuevo = new Cliente(idCliente, nombre, correo, direccion);
        try {
            clienteRepository.agregar(nuevo);
            System.out.println("Cliente registrado.");
            return nuevo;
        } catch (DatoDuplicadoException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private MedioPago solicitarMedioPago() {
        System.out.println("Seleccione medio de pago:");
        MedioPago[] medios = MedioPago.values();
        for (int i = 0; i < medios.length; i++) {
            System.out.println((i + 1) + ". " + medios[i]);
        }
        while (true) {
            int opcion = EntradaConsolaUtil.leerEntero(scanner, "Opción: ");
            if (opcion >= 1 && opcion <= medios.length) {
                return medios[opcion - 1];
            }
            System.out.println("Opción no válida.");
        }
    }

    private void agregarItemsAVenta(Venta venta) {
        boolean seguir = true;
        while (seguir) {
            String isbn = EntradaConsolaUtil.leerTexto(scanner, "ISBN del libro (0 para terminar): ");
            if ("0".equals(isbn)) {
                seguir = false;
                continue;
            }
            Optional<Libro> libroOpt = catalogoRepository.buscarPorIsbn(isbn);
            if (libroOpt.isEmpty()) {
                System.out.println("No se encontró el libro indicado.");
                continue;
            }
            Libro libro = libroOpt.get();
            int cantidad = EntradaConsolaUtil.leerEntero(scanner, "Cantidad: ");
            if (cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a cero.");
                continue;
            }
            double precio = libro.getPrecio();
            venta.agregarItem(new ItemVenta(libro, cantidad, precio));
            System.out.println("Ítem agregado.");
        }
    }

    private void administrarItems() {
        String idVenta = EntradaConsolaUtil.leerTexto(scanner, "ID de la venta: ");
        Optional<Venta> ventaOpt = ventaRepository.buscarPorId(idVenta);
        if (ventaOpt.isEmpty()) {
            System.out.println("No se encontró la venta.");
            return;
        }
        Venta venta = ventaOpt.get();
        boolean continuar = true;
        while (continuar) {
            System.out.println("\nVenta " + venta.getIdVenta() + " - Total actual: $" + venta.calcularTotal());
            for (int i = 0; i < venta.getItems().size(); i++) {
                ItemVenta item = venta.getItems().get(i);
                System.out.println((i + 1) + ". " + item.getLibro().getTitulo() + " x" + item.getCantidad() + " = $" + item.calcularSubtotal());
            }
            System.out.println("a. Editar ítem");
            System.out.println("b. Eliminar ítem");
            System.out.println("c. Agregar nuevo ítem");
            System.out.println("0. Volver");
            String opcion = EntradaConsolaUtil.leerTexto(scanner, "Seleccione opción: ");
            switch (opcion) {
                case "a":
                    editarItem(venta);
                    break;
                case "b":
                    eliminarItem(venta);
                    break;
                case "c":
                    agregarItemsAVenta(venta);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        try {
            ventaRepository.actualizar(venta);
            System.out.println("Venta actualizada.");
        } catch (DatoNoEncontradoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void editarItem(Venta venta) {
        if (venta.getItems().isEmpty()) {
            System.out.println("La venta no tiene ítems para editar.");
            return;
        }
        int indice = EntradaConsolaUtil.leerEntero(scanner, "Número de ítem a editar: ") - 1;
        if (indice < 0 || indice >= venta.getItems().size()) {
            System.out.println("Índice fuera de rango.");
            return;
        }
        int cantidad = EntradaConsolaUtil.leerEntero(scanner, "Nueva cantidad: ");
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }
        double precio = EntradaConsolaUtil.leerDecimal(scanner, "Nuevo precio unitario: ");
        venta.editarItem(indice, cantidad, precio);
        System.out.println("Ítem editado.");
    }

    private void eliminarItem(Venta venta) {
        if (venta.getItems().isEmpty()) {
            System.out.println("La venta no tiene ítems para eliminar.");
            return;
        }
        int indice = EntradaConsolaUtil.leerEntero(scanner, "Número de ítem a eliminar: ") - 1;
        if (indice < 0 || indice >= venta.getItems().size()) {
            System.out.println("Índice fuera de rango.");
            return;
        }
        venta.eliminarItem(indice);
        System.out.println("Ítem eliminado.");
    }

    private void eliminarVenta() {
        String idVenta = EntradaConsolaUtil.leerTexto(scanner, "ID de la venta a eliminar: ");
        try {
            ventaRepository.eliminar(idVenta);
            System.out.println("Venta eliminada.");
        } catch (DatoNoEncontradoException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
