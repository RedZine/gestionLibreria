package gestionlibreria.repository;

import gestionlibreria.model.Venta;
import java.util.ArrayList;

public class VentaRepository {
    private final ArrayList<Venta> listaVentas = new ArrayList<Venta>();

    public void agregar(Venta venta) { listaVentas.add(venta); }

    public ArrayList<Venta> listar() { return listaVentas; }

    public int siguienteId() { return listaVentas.size() + 1; }
}
