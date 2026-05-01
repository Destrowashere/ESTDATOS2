package biblioteca;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Representa un libro en el sistema de gestión de biblioteca.
 * Cada libro mantiene su propia cola de espera (Queue - FIFO) de usuarios.
 */
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private boolean disponible;
    private Queue<Usuario> colaEspera; // ESTRUCTURA: Queue (Cola FIFO)

    public Libro(int id, String titulo, String autor, String categoria) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.disponible = true;
        this.colaEspera = new LinkedList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getId()                    { return id; }
    public String getTitulo()             { return titulo; }
    public String getAutor()              { return autor; }
    public String getCategoria()          { return categoria; }
    public boolean isDisponible()         { return disponible; }
    public Queue<Usuario> getColaEspera() { return colaEspera; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    // ── Utilidades de Cola ───────────────────────────────────────────────────

    /** Agrega un usuario al final de la cola de espera. */
    public void agregarAColaEspera(Usuario usuario) {
        colaEspera.offer(usuario);
    }

    /** Retira y retorna al primer usuario de la cola (FIFO). */
    public Usuario siguienteEnCola() {
        return colaEspera.poll();
    }

    public boolean hayEspera() {
        return !colaEspera.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] \"%s\" - %s | Categoria: %s | %s",
                id, titulo, autor, categoria,
                disponible ? "DISPONIBLE" : "PRESTADO");
    }
}
