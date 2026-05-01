package biblioteca;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un usuario registrado en la biblioteca.
 * Mantiene una lista de los libros que tiene actualmente en préstamo.
 */
public class Usuario {

    private int id;
    private String nombre;
    private List<Libro> librosPrestados; // ESTRUCTURA: ArrayList (Lista dinámica)

    public Usuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.librosPrestados = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getId()                         { return id; }
    public String getNombre()                  { return nombre; }
    public List<Libro> getLibrosPrestados()    { return librosPrestados; }

    // ── Utilidades de Lista ──────────────────────────────────────────────────

    public void agregarLibro(Libro libro) {
        librosPrestados.add(libro);
    }

    public void devolverLibro(Libro libro) {
        librosPrestados.remove(libro);
    }

    public boolean tieneLibro(Libro libro) {
        return librosPrestados.contains(libro);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] %s | Libros en prestamo: %d",
                id, nombre, librosPrestados.size());
    }
}
