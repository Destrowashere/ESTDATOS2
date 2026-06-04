package biblioteca;

/**
 * Arista ponderada y tipada en el grafo bipartito usuario-libro.
 */
public class Arista {

    private final String destino;
    private int peso;
    private TipoRelacion tipo;

    public Arista(String destino, int peso, TipoRelacion tipo) {
        this.destino = destino;
        this.peso = peso;
        this.tipo = tipo;
    }

    public String getDestino()       { return destino; }
    public int getPeso()             { return peso; }
    public TipoRelacion getTipo()    { return tipo; }

    public void setPeso(int peso)           { this.peso = peso; }
    public void setTipo(TipoRelacion tipo)  { this.tipo = tipo; }

    @Override
    public String toString() {
        return String.format("-> %s [%s, peso=%d]", destino, tipo, peso);
    }
}
