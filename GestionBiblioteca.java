package biblioteca;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Cerebro del sistema: contiene todas las colecciones y la lógica de negocio.
 *
 * Estructuras utilizadas:
 *  - ArrayList<Libro>    → Catálogo de libros       (Lista dinámica)
 *  - ArrayList<Usuario>  → Registro de usuarios     (Lista dinámica)
 *  - Stack<Libro>        → Historial de devoluciones (Pila LIFO)
 *  - Queue<Usuario>      → Cola de espera por libro  (Cola FIFO - dentro de Libro)
 *  - String[]            → Categorías disponibles    (Arreglo estático)
 */
public class GestionBiblioteca {

    // ── ESTRUCTURA: Arreglo estático - Categorías fijas ──────────────────────
    private static final String[] CATEGORIAS = {
        "Terror", "Ciencia", "Historia", "Fantasia", "Romance",
        "Tecnologia", "Filosofia", "Biografia"
    };

    // ── ESTRUCTURA: ArrayList - Catálogos dinámicos ──────────────────────────
    private List<Libro>   catalogoLibros;
    private List<Usuario> registroUsuarios;

    // ── ESTRUCTURA: Stack (Pila LIFO) - Historial de devoluciones ────────────
    private Stack<Libro> historialDevoluciones;

    // Contadores de IDs automáticos
    private int contadorIdLibro   = 1;
    private int contadorIdUsuario = 1;

    public GestionBiblioteca() {
        catalogoLibros        = new ArrayList<>();
        registroUsuarios      = new ArrayList<>();
        historialDevoluciones = new Stack<>();
        cargarDatosDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  REGISTRO
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Registra un libro si la categoría es válida.
     * @return true si se registró exitosamente, false si la categoría no existe.
     */
    public boolean registrarLibro(String titulo, String autor, String categoria) {
        if (!categoriaValida(categoria)) {
            return false;
        }
        Libro libro = new Libro(contadorIdLibro++, titulo, autor, categoria);
        catalogoLibros.add(libro);
        return true;
    }

    /** Registra un nuevo usuario en el sistema. */
    public void registrarUsuario(String nombre) {
        Usuario usuario = new Usuario(contadorIdUsuario++, nombre);
        registroUsuarios.add(usuario);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  BÚSQUEDA
    // ═══════════════════════════════════════════════════════════════════════════

    /** Busca un libro por su ID. Retorna null si no existe. */
    public Libro buscarLibroPorId(int id) {
        for (Libro libro : catalogoLibros) {
            if (libro.getId() == id) return libro;
        }
        return null;
    }

    /**
     * Busca libros cuyo título contenga el texto indicado (insensible a mayúsculas).
     * @return Lista de coincidencias (puede estar vacía).
     */
    public List<Libro> buscarLibroPorTitulo(String titulo) {
        List<Libro> resultados = new ArrayList<>();
        for (Libro libro : catalogoLibros) {
            if (libro.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultados.add(libro);
            }
        }
        return resultados;
    }

    /** Busca un usuario por su ID. Retorna null si no existe. */
    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario usuario : registroUsuarios) {
            if (usuario.getId() == id) return usuario;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  PRÉSTAMO Y DEVOLUCIÓN
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Intenta realizar un préstamo.
     * - Si el libro está disponible → se presta directamente.
     * - Si no está disponible → el usuario entra a la cola de espera.
     * @return Mensaje descriptivo del resultado.
     */
    public String realizarPrestamo(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro con ID " + idLibro + " no encontrado.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario con ID " + idUsuario + " no encontrado.";

        if (usuario.tieneLibro(libro)) {
            return "AVISO: El usuario ya tiene este libro en prestamo.";
        }

        // Verificar si ya está en la cola de espera
        if (libro.getColaEspera().contains(usuario)) {
            return "AVISO: El usuario ya esta en la lista de espera para este libro.";
        }

        if (libro.isDisponible()) {
            libro.setDisponible(false);
            usuario.agregarLibro(libro);
            return "EXITO: Prestamo realizado. \"" + libro.getTitulo()
                    + "\" prestado a " + usuario.getNombre() + ".";
        } else {
            // Cola de espera (Queue FIFO)
            libro.agregarAColaEspera(usuario);
            int posicion = libro.getColaEspera().size();
            return "ESPERA: El libro no esta disponible. " + usuario.getNombre()
                    + " agregado a la cola. Posicion: " + posicion + ".";
        }
    }

    /**
     * Procesa la devolución de un libro.
     * - Empuja el libro a la pila de historial (Stack LIFO).
     * - Si hay cola de espera, asigna automáticamente al siguiente usuario.
     * @return Mensaje descriptivo del resultado.
     */
    public String devolverLibro(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro con ID " + idLibro + " no encontrado.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario con ID " + idUsuario + " no encontrado.";

        if (!usuario.tieneLibro(libro)) {
            return "ERROR: El usuario no tiene este libro en prestamo.";
        }

        // Procesar devolución
        usuario.devolverLibro(libro);
        historialDevoluciones.push(libro); // PILA LIFO: push

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("EXITO: \"").append(libro.getTitulo())
               .append("\" devuelto por ").append(usuario.getNombre()).append(".\n");

        // Verificar cola de espera (Queue FIFO: poll)
        if (libro.hayEspera()) {
            Usuario siguiente = libro.siguienteEnCola();
            libro.setDisponible(false);
            siguiente.agregarLibro(libro);
            mensaje.append("  >> Asignado automaticamente a: ").append(siguiente.getNombre())
                   .append(" (siguiente en cola).");
        } else {
            libro.setDisponible(true);
            mensaje.append("  >> El libro esta ahora DISPONIBLE.");
        }

        return mensaje.toString();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  VISUALIZACIÓN DE ESTRUCTURAS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Retorna todos los libros del catálogo. */
    public List<Libro> getCatalogoLibros() {
        return catalogoLibros;
    }

    /** Retorna todos los usuarios registrados. */
    public List<Usuario> getRegistroUsuarios() {
        return registroUsuarios;
    }

    /**
     * Muestra el historial de devoluciones desde la cima de la pila (LIFO).
     * No destruye la pila: solo itera sobre ella.
     */
    public String mostrarHistorialDevoluciones() {
        if (historialDevoluciones.isEmpty()) {
            return "  (Sin devoluciones registradas aun)";
        }
        StringBuilder sb = new StringBuilder();
        // Iterar desde la cima (índice mayor = más reciente)
        for (int i = historialDevoluciones.size() - 1; i >= 0; i--) {
            sb.append("  ").append(i + 1).append(". ")
              .append(historialDevoluciones.get(i).getTitulo())
              .append(" (").append(historialDevoluciones.get(i).getAutor()).append(")\n");
        }
        return sb.toString().trim();
    }

    /**
     * Muestra la cola de espera de un libro específico.
     */
    public String mostrarColaEspera(int idLibro) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "  ERROR: Libro no encontrado.";

        if (!libro.hayEspera()) {
            return "  (Sin usuarios en lista de espera para \"" + libro.getTitulo() + "\")";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("  Lista de espera para \"").append(libro.getTitulo()).append("\":\n");
        int pos = 1;
        for (Usuario u : libro.getColaEspera()) {
            sb.append("    ").append(pos++).append(". ").append(u.getNombre()).append("\n");
        }
        return sb.toString().trim();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CATEGORÍAS (Arreglo estático)
    // ═══════════════════════════════════════════════════════════════════════════

    public boolean categoriaValida(String categoria) {
        for (String cat : CATEGORIAS) {
            if (cat.equalsIgnoreCase(categoria)) return true;
        }
        return false;
    }

    public String[] getCategorias() {
        return CATEGORIAS;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ═══════════════════════════════════════════════════════════════════════════

    private void cargarDatosDemo() {
        registrarLibro("El Resplandor",              "Stephen King",      "Terror");
        registrarLibro("Cosmos",                     "Carl Sagan",        "Ciencia");
        registrarLibro("Sapiens",                    "Yuval Noah Harari", "Historia");
        registrarLibro("El Senor de los Anillos",    "J.R.R. Tolkien",    "Fantasia");
        registrarLibro("Breve Historia del Tiempo",  "Stephen Hawking",   "Ciencia");

        registrarUsuario("Ana Garcia");
        registrarUsuario("Carlos Lopez");
        registrarUsuario("Maria Perez");
    }
}
