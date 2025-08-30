package gestionlibreria.model;

public class Promocion {
    private int id;
    private String tipo;       // "PORCENTAJE" o "FIJO"
    private String alcance;    // "CATEGORIA" o "GENERAL"
    private String categoria;  // opcional, si aplica a una categoría
    private double valor;      // % o monto fijo
    private boolean activa;

    public Promocion() {}

    public Promocion(int id, String tipo, String alcance, String categoria, double valor, boolean activa) {
        this.id = id;
        this.tipo = tipo;
        this.alcance = alcance;
        this.categoria = categoria;
        this.valor = valor;
        this.activa = activa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAlcance() { return alcance; }
    public void setAlcance(String alcance) { this.alcance = alcance; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
