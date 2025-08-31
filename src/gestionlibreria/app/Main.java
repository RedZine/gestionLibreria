package gestionlibreria.app;

import gestionlibreria.model.Categoria;
import gestionlibreria.model.Cliente;
import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Promocion;
import gestionlibreria.model.Venta;

import gestionlibreria.repository.CategoriaRepository;
import gestionlibreria.repository.ClienteRepository;
import gestionlibreria.repository.PromocionRepository;
import gestionlibreria.repository.VentaRepository;

import gestionlibreria.util.DataSeeder;
import gestionlibreria.util.PromocionHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    // Opciones visibles al usuario (sin enum)
    private static final String[] OPCIONES_MEDIO_PAGO   = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
    private static final String[] OPCIONES_TIPO_PROMO   = {"PORCENTAJE", "FIJO"};
    private static final String[] OPCIONES_ALCANCE_PROMO= {"GENERAL", "CATEGORIA"};

    private static final CategoriaRepository categoriaRepositorio = new CategoriaRepository();
    private static final ClienteRepository clienteRepositorio     = new ClienteRepository();
    private static final PromocionRepository promocionRepositorio = new PromocionRepository();
    private static final VentaRepository ventaRepositorio         = new VentaRepository();

    public static void main(String[] args) throws IOException {
        DataSeeder.seed(categoriaRepositorio, clienteRepositorio, promocionRepositorio);

        BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
        String opcion;

        do {
            encabezado("Gestion de Libreria");
            System.out.println("1) Insertar libro en categoria");
            System.out.println("2) Mostrar libros de una categoria");
            System.out.println("3) Mostrar categorias");
            System.out.println("4) Mostrar sugerencias por categoria");
            System.out.println("5) Registrar venta");
            System.out.println("6) Mostrar ventas");
            System.out.println("7) Crear promocion");
            System.out.println("8) Mostrar promociones");
            System.out.println("0) Salir");
            linea();
            System.out.print("> ");
            opcion = lector.readLine();

            switch (opcion) {
                case "1": insertarLibroEnCategoria(lector); break;
                case "2": mostrarLibrosDeCategoria(lector); break;
                case "3": mostrarCategorias(); break;
                case "4": mostrarSugerencias(lector); break;
                case "5": registrarVenta(lector); break;
                case "6": mostrarVentas(); break;
                case "7": crearPromocion(lector); break;
                case "8": mostrarPromociones(); break;
                case "0": System.out.println("Saliendo..."); break;
                default:  System.out.println("Opcion no valida."); pausa(lector);
            }
        } while (!"0".equals(opcion));
    }

    // ===== UI helpers =====
    private static void encabezado(String titulo){
        linea();
        System.out.println(titulo);
        linea();
    }
    private static void linea(){ System.out.println("------------------------------------------------------------"); }
    private static void pausa(BufferedReader lector) throws IOException {
        System.out.print("Enter para continuar...");
        lector.readLine();
    }

    // Formatea pesos chilenos SIN decimales y con puntos de miles (sin usar clases avanzadas)
    private static String dinero(double valor){
        long pesos = Math.round(valor);              // sin decimales
        String s = Long.toString(pesos);
        StringBuilder sb = new StringBuilder();
        int c = 0;
        for (int i = s.length()-1; i >= 0; i--){
            sb.append(s.charAt(i));
            c++;
            if (c == 3 && i != 0){
                sb.append('.');
                c = 0;
            }
        }
        return "$" + sb.reverse().toString();
    }

    // Muestra opciones numeradas y devuelve la seleccion (texto)
    private static String elegirDeLista(BufferedReader lector, String titulo, String[] opciones) throws IOException {
        System.out.println(titulo + ":");
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ") " + opciones[i]);
        }
        int indice = -1;
        do {
            System.out.print("Elija una opcion [1-" + opciones.length + "]: ");
            String entrada = lector.readLine();
            try { indice = Integer.parseInt(entrada) - 1; }
            catch (Exception e) { indice = -1; }
            if (indice < 0 || indice >= opciones.length) System.out.println("Opcion invalida.");
        } while (indice < 0 || indice >= opciones.length);
        return opciones[indice];
    }

    // ===== Categorias y Libros =====
    private static void mostrarCategorias() {
        encabezado("Categorias");
        ArrayList<Categoria> cats = categoriaRepositorio.listarCategorias();
        if (cats.isEmpty()){ System.out.println("No hay categorias."); return; }

        System.out.printf("%-6s | %-25s | %-8s%n", "ID", "Nombre", "#Libros");
        linea();
        for (Categoria c : cats) {
            System.out.printf("%-6d | %-25s | %-8d%n",
                    c.getId(), c.getNombre(), c.getLibros().size());
        }
    }

    private static void insertarLibroEnCategoria(BufferedReader lector) throws IOException {
        mostrarCategorias();
        linea();
        System.out.print("ID de categoria: ");
        int idCategoria = leerEntero(lector);

        System.out.print("ISBN: ");                 String isbn = lector.readLine();
        System.out.print("Titulo: ");               String titulo = lector.readLine();
        System.out.print("Autor: ");                String autor = lector.readLine();
        System.out.print("Categoria (texto): ");    String categoriaTexto = lector.readLine();
        System.out.print("Precio: ");               double precio = leerDecimal(lector);
        System.out.print("Stock: ");                int stock = leerEntero(lector);

        boolean ok = categoriaRepositorio.agregarLibro(idCategoria, isbn, titulo, autor, categoriaTexto, precio, stock);
        System.out.println(ok ? "Libro agregado." : "Categoria no encontrada.");
        linea();
    }

    private static void mostrarLibrosDeCategoria(BufferedReader lector) throws IOException {
        mostrarCategorias();
        linea();
        System.out.print("ID de categoria: ");
        int idCategoria = leerEntero(lector);

        ArrayList<Libro> lista = categoriaRepositorio.listarLibrosDeCategoria(idCategoria);
        encabezado("Libros de la categoria " + idCategoria);
        if (lista.isEmpty()){ System.out.println("Sin libros o categoria inexistente."); return; }

        System.out.printf("%-13s | %-26s | %-18s | %-14s | %-12s | %5s%n",
                "ISBN","Titulo","Autor","Categoria","Precio","Stock");
        linea();
        for (Libro l : lista) {
            System.out.printf("%-13s | %-26s | %-18s | %-14s | %-12s | %5d%n",
                    l.getIsbn(), l.getTitulo(), l.getAutor(), l.getCategoria(), dinero(l.getPrecio()), l.getStock());
        }
    }

    private static void mostrarSugerencias(BufferedReader lector) throws IOException {
        encabezado("Sugerencias por categoria");
        System.out.print("ISBN base: ");
        String isbnBase = lector.readLine();

        Libro base = categoriaRepositorio.buscarLibroPorIsbn(isbnBase);
        if (base == null){ System.out.println("No existe ese ISBN."); return; }

        ArrayList<Libro> sug = categoriaRepositorio.sugerenciasPorCategoria(base.getCategoria(), base.getIsbn());
        System.out.println("Categoria base: " + base.getCategoria());
        linea();
        if (sug.isEmpty()){ System.out.println("Sin sugerencias."); return; }

        System.out.printf("%-13s | %-26s | %-18s%n", "ISBN","Titulo","Autor");
        linea();
        for (Libro l : sug) {
            System.out.printf("%-13s | %-26s | %-18s%n", l.getIsbn(), l.getTitulo(), l.getAutor());
        }
    }

    // ===== Ventas =====
    private static void registrarVenta(BufferedReader lector) throws IOException {
        encabezado("Registrar venta");
        mostrarClientesTabla();
        linea();
        System.out.print("ID de cliente: ");
        int idCliente = leerEntero(lector);

        Cliente cli = clienteRepositorio.buscarPorId(idCliente);
        if (cli == null){ System.out.println("Cliente no existe."); return; }

        int idVenta = ventaRepositorio.siguienteId();
        System.out.print("Fecha (YYYY-MM-DD): "); String fecha = lector.readLine();

        String medioPago = elegirDeLista(lector, "Medio de pago", OPCIONES_MEDIO_PAGO);

        Venta venta = new Venta(idVenta, idCliente, fecha, medioPago);

        while (true) {
            System.out.print("ISBN (o vacio para terminar): ");
            String isbn = lector.readLine();
            if (isbn == null || isbn.trim().isEmpty()) break;

            Libro libro = categoriaRepositorio.buscarLibroPorIsbn(isbn);
            if (libro == null){ System.out.println("No existe ISBN."); continue; }

            System.out.print("Cantidad: ");
            int cantidad = leerEntero(lector);

            boolean ok = categoriaRepositorio.disminuirStock(isbn, cantidad);
            if (!ok){
                System.out.println("Stock insuficiente. Disponible: " + (libro != null ? libro.getStock() : 0));
                continue;
            }

            venta.agregarItem(isbn, cantidad, libro.getPrecio());
            imprimirCarrito(venta);
        }

        if (venta.getItems().isEmpty()){
            System.out.println("Venta vacia. Cancelada.");
            return;
        }

        double subtotal  = venta.getSubtotal();
        double descuento = PromocionHelper.calcularDescuento(venta, promocionRepositorio.listarActivas(), categoriaRepositorio);
        double total     = subtotal - descuento;

        encabezado("Resumen de Venta");
        System.out.println("Venta #" + venta.getId() + "  Cliente: " + cli.getNombre());
        imprimirCarrito(venta);
        System.out.println("Subtotal : " + dinero(subtotal));
        System.out.println("Descuento: " + dinero(descuento));
        System.out.println("TOTAL    : " + dinero(total));
        linea();

        ventaRepositorio.agregar(venta);
        System.out.println("Venta registrada.");
    }

    private static void imprimirCarrito(Venta venta){
        ArrayList<ItemVenta> items = venta.getItems();
        if (items.isEmpty()){ System.out.println("Carrito vacio."); return; }
        System.out.printf("%-13s | %8s | %-12s | %-12s%n", "ISBN", "Cantidad", "Precio", "Subtotal");
        linea();
        for (ItemVenta it : items) {
            System.out.printf("%-13s | %8d | %-12s | %-12s%n",
                    it.getIsbn(), it.getCantidad(), dinero(it.getPrecioUnitario()), dinero(it.getSubtotal()));
        }
        System.out.println("Subtotal actual: " + dinero(venta.getSubtotal()));
        linea();
    }

    private static void mostrarVentas() {
        encabezado("Ventas");
        ArrayList<Venta> ventas = ventaRepositorio.listar();
        if (ventas.isEmpty()){ System.out.println("No hay ventas registradas."); return; }

        System.out.printf("%-4s | %-8s | %-10s | %-12s | %-7s | %-12s | %-12s%n",
                "ID","Cliente","Fecha","Pago","#Items","Subtotal","Total");
        linea();
        for (Venta v : ventas) {
            double sub = v.getSubtotal();
            double desc = PromocionHelper.calcularDescuento(v, promocionRepositorio.listarActivas(), categoriaRepositorio);
            double tot = sub - desc;
            System.out.printf("%-4d | %-8d | %-10s | %-12s | %-7d | %-12s | %-12s%n",
                    v.getId(), v.getIdCliente(), v.getFecha(), v.getMedioPago(), v.getItems().size(),
                    dinero(sub), dinero(tot));
        }
    }

    private static void mostrarClientesTabla() {
        System.out.println("-- Clientes --");
        ArrayList<Cliente> lista = clienteRepositorio.listar();
        if (lista.isEmpty()){ System.out.println("No hay clientes."); return; }
        System.out.printf("%-6s | %-20s | %-20s%n", "ID", "Nombre", "Email");
        linea();
        for (Cliente c : lista) {
            System.out.printf("%-6d | %-20s | %-20s%n", c.getId(), c.getNombre(), c.getEmail());
        }
    }

    // ===== Promociones =====
    private static void crearPromocion(BufferedReader lector) throws IOException {
        encabezado("Crear promocion");

        String tipo    = elegirDeLista(lector, "Tipo de promocion", OPCIONES_TIPO_PROMO);
        String alcance = elegirDeLista(lector, "Alcance de la promocion", OPCIONES_ALCANCE_PROMO);

        String nombreCategoria = null;
        if ("CATEGORIA".equalsIgnoreCase(alcance)) {
            mostrarCategorias();
            System.out.print("Nombre de categoria: ");
            nombreCategoria = lector.readLine();
        }

        System.out.print("Valor (porcentaje entero o monto en pesos): ");
        double valor = leerDecimal(lector);

        int idPromocion = promocionRepositorio.listarTodas().size() + 1;
        Promocion p = new Promocion(idPromocion, tipo, alcance, nombreCategoria, valor, true);
        promocionRepositorio.agregar(p);

        System.out.println("Promocion creada y activada.");
    }

    private static void mostrarPromociones() {
        encabezado("Promociones");
        ArrayList<Promocion> promos = promocionRepositorio.listarTodas();
        if (promos.isEmpty()){ System.out.println("No hay promociones."); return; }

        System.out.printf("%-4s | %-11s | %-10s | %-14s | %-12s | %-7s%n",
                "ID","Tipo","Alcance","Categoria","Valor","Activa");
        linea();
        for (Promocion p : promos) {
            String cat = (p.getCategoria() != null) ? p.getCategoria() : "-";
            String valorStr = "PORCENTAJE".equalsIgnoreCase(p.getTipo())
                    ? (Math.round(p.getValor()) + "%")
                    : dinero(p.getValor());
            System.out.printf("%-4d | %-11s | %-10s | %-14s | %-12s | %-7s%n",
                    p.getId(), p.getTipo(), p.getAlcance(), cat, valorStr, String.valueOf(p.isActiva()));
        }
    }

    // ===== Lectura segura =====
    private static int leerEntero(BufferedReader lector) throws IOException {
        try { return Integer.parseInt(lector.readLine()); } catch (Exception e) { return 0; }
    }
    private static double leerDecimal(BufferedReader lector) throws IOException {
        try { return Double.parseDouble(lector.readLine()); } catch (Exception e) { return 0.0; }
    }
}
