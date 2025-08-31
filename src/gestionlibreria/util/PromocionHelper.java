package gestionlibreria.util;

import gestionlibreria.model.ItemVenta;
import gestionlibreria.model.Libro;
import gestionlibreria.model.Promocion;
import gestionlibreria.model.Venta;
import gestionlibreria.repository.CategoriaRepository;

import java.util.ArrayList;

public class PromocionHelper {

    public static double calcularDescuento(Venta venta, ArrayList<Promocion> promocionesActivas,
                                           CategoriaRepository categoriaRepositorio) {
        double subtotalVenta = venta.getSubtotal();
        double descuentoTotal = 0.0;

        for (Promocion promocion : promocionesActivas) {
            if (!promocion.isActiva()) continue;

            if ("PORCENTAJE".equalsIgnoreCase(promocion.getTipo())) {
                if ("GENERAL".equalsIgnoreCase(promocion.getAlcance())) {
                    descuentoTotal += subtotalVenta * (promocion.getValor() / 100.0);
                } else if ("CATEGORIA".equalsIgnoreCase(promocion.getAlcance())) {
                    double baseCategoria = calcularSubtotalDeCategoria(venta, promocion.getCategoria(), categoriaRepositorio);
                    descuentoTotal += baseCategoria * (promocion.getValor() / 100.0);
                }
            } else if ("FIJO".equalsIgnoreCase(promocion.getTipo())) {
                if ("GENERAL".equalsIgnoreCase(promocion.getAlcance())) {
                    descuentoTotal += promocion.getValor();
                } else if ("CATEGORIA".equalsIgnoreCase(promocion.getAlcance())) {
                    double baseCategoria = calcularSubtotalDeCategoria(venta, promocion.getCategoria(), categoriaRepositorio);
                    if (promocion.getValor() < baseCategoria) {
                        descuentoTotal += promocion.getValor();
                    } else {
                        descuentoTotal += baseCategoria;
                    }
                }
            }
        }

        if (descuentoTotal > subtotalVenta) {
            descuentoTotal = subtotalVenta;
        }
        return descuentoTotal;
    }

    private static double calcularSubtotalDeCategoria(Venta venta, String nombreCategoria,
                                                      CategoriaRepository categoriaRepositorio) {
        double subtotal = 0.0;
        ArrayList<ItemVenta> items = venta.getItems();

        for (ItemVenta item : items) {
            Libro libro = categoriaRepositorio.buscarLibroPorIsbn(item.getIsbn());
            if (libro != null && libro.getCategoria().equalsIgnoreCase(nombreCategoria)) {
                subtotal += item.getSubtotal();
            }
        }
        return subtotal;
    }
}
