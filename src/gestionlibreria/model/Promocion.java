package gestionlibreria.model;

import gestionlibreria.model.enums.AlcancePromo;
import gestionlibreria.model.enums.TipoPromo;

/**
 * Describe una promoción aplicada a libros o ventas.
 */
public class Promocion {
    private String id;
    private String descripcion;
    private TipoPromo tipo;
    private AlcancePromo alcance;
    private double valor;

    /**
     * Crea una promoción parametrizable.
     */
    public Promocion(String id, String descripcion, TipoPromo tipo, AlcancePromo alcance, double valor) {
        this.id = id;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.alcance = alcance;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoPromo getTipo() {
        return tipo;
    }

    public void setTipo(TipoPromo tipo) {
        this.tipo = tipo;
    }

    public AlcancePromo getAlcance() {
        return alcance;
    }

    public void setAlcance(AlcancePromo alcance) {
        this.alcance = alcance;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
