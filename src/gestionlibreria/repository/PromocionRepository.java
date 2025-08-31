package gestionlibreria.repository;

import gestionlibreria.model.Promocion;
import java.util.ArrayList;

public class PromocionRepository {
    private final ArrayList<Promocion> listaPromociones = new ArrayList<Promocion>();

    public void agregar(Promocion promocion) { listaPromociones.add(promocion); }

    public ArrayList<Promocion> listarTodas() { return listaPromociones; }

    public ArrayList<Promocion> listarActivas() {
        ArrayList<Promocion> activas = new ArrayList<Promocion>();
        for (Promocion p : listaPromociones) {
            if (p.isActiva()) activas.add(p);
        }
        return activas;
    }
}
