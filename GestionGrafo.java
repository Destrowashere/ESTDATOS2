package biblioteca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Cerebro del sistema - FASE 3: Grafo bipartito dirigido y ponderado.
 *
 * Modela interacciones usuario-libro con aristas tipadas:
 *   PRESTAMO_ACTIVO, ESPERA, HISTORIAL
 *
 * Conserva estructuras de fases anteriores:
 *   - ArrayList<Libro>   -> Catalogo
 *   - ArrayList<Usuario> -> Registro
 *   - Stack<Libro>       -> Historial devoluciones (LIFO)
 *   - Queue<Usuario>     -> Cola espera por libro (FIFO, en Libro)
 *   - GrafoBiblioteca    -> Relaciones y recomendaciones (BFS)
 */
public class GestionGrafo {

    private static final String[] CATEGORIAS = {
        "Terror", "Ciencia", "Historia", "Fantasia", "Romance",
        "Tecnologia", "Filosofia", "Biografia"
    };

    private List<Libro> catalogoLibros;
    private List<Usuario> registroUsuarios;
    private Stack<Libro> historialDevoluciones;
    private GrafoBiblioteca grafo;

    private int contadorIdLibro   = 1;
    private int contadorIdUsuario = 1;

    public GestionGrafo() {
        catalogoLibros        = new ArrayList<>();
        registroUsuarios      = new ArrayList<>();
        historialDevoluciones = new Stack<>();
        grafo                 = new GrafoBiblioteca();
        cargarDatosDemo();
    }

    public GrafoBiblioteca getGrafo() { return grafo; }

    // ═══════════════════════════════════════════════════════════════════════
    //  REGISTRO
    // ═══════════════════════════════════════════════════════════════════════

    public boolean registrarLibro(String titulo, String autor, String categoria) {
        if (!categoriaValida(categoria)) return false;
        Libro libro = new Libro(contadorIdLibro++, titulo, autor, categoria);
        catalogoLibros.add(libro);
        grafo.agregarNodoLibro(libro.getId());
        return true;
    }

    public void registrarUsuario(String nombre) {
        Usuario u = new Usuario(contadorIdUsuario++, nombre);
        registroUsuarios.add(u);
        grafo.agregarNodoUsuario(u.getId());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BUSQUEDA
    // ═══════════════════════════════════════════════════════════════════════

    public Libro buscarLibroPorId(int id) {
        for (Libro l : catalogoLibros) {
            if (l.getId() == id) return l;
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario u : registroUsuarios) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PRESTAMO Y DEVOLUCION (sincronizado con grafo)
    // ═══════════════════════════════════════════════════════════════════════

    public String realizarPrestamo(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro con ID " + idLibro + " no encontrado.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario con ID " + idUsuario + " no encontrado.";

        String claveU = GrafoBiblioteca.claveUsuario(idUsuario);
        String claveL = GrafoBiblioteca.claveLibro(idLibro);

        if (usuario.tieneLibro(libro)) {
            return "AVISO: El usuario ya tiene este libro en prestamo.";
        }
        if (libro.getColaEspera().contains(usuario)) {
            return "AVISO: El usuario ya esta en la lista de espera para este libro.";
        }

        if (libro.isDisponible()) {
            libro.setDisponible(false);
            usuario.agregarLibro(libro);
            grafo.eliminarAristaPorTipo(claveU, claveL, TipoRelacion.ESPERA);
            grafo.agregarArista(claveU, claveL, TipoRelacion.PRESTAMO_ACTIVO, 1);
            return "EXITO: Prestamo realizado. \"" + libro.getTitulo()
                    + "\" prestado a " + usuario.getNombre()
                    + ".\n  >> Arista grafo: " + claveU + " --[PRESTAMO_ACTIVO]--> " + claveL;
        } else {
            libro.agregarAColaEspera(usuario);
            grafo.agregarArista(claveU, claveL, TipoRelacion.ESPERA, 1);
            int posicion = libro.getColaEspera().size();
            return "ESPERA: El libro no esta disponible. " + usuario.getNombre()
                    + " agregado a la cola. Posicion: " + posicion + ".\n"
                    + "  >> Arista grafo: " + claveU + " --[ESPERA]--> " + claveL;
        }
    }

    public String devolverLibro(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro con ID " + idLibro + " no encontrado.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario con ID " + idUsuario + " no encontrado.";

        if (!usuario.tieneLibro(libro)) {
            return "ERROR: El usuario no tiene este libro en prestamo.";
        }

        String claveU = GrafoBiblioteca.claveUsuario(idUsuario);
        String claveL = GrafoBiblioteca.claveLibro(idLibro);

        usuario.devolverLibro(libro);
        historialDevoluciones.push(libro);

        grafo.eliminarAristaPorTipo(claveU, claveL, TipoRelacion.PRESTAMO_ACTIVO);
        int pesoHistorial = 1;
        Arista hist = grafo.buscarArista(claveU, claveL);
        if (hist != null && hist.getTipo() == TipoRelacion.HISTORIAL) {
            pesoHistorial = hist.getPeso() + 1;
            grafo.eliminarArista(claveU, claveL);
        }
        grafo.agregarArista(claveU, claveL, TipoRelacion.HISTORIAL, pesoHistorial);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("EXITO: \"").append(libro.getTitulo())
               .append("\" devuelto por ").append(usuario.getNombre()).append(".\n");
        mensaje.append("  >> Arista grafo: ").append(claveU)
               .append(" --[HISTORIAL, peso=").append(pesoHistorial).append("]--> ")
               .append(claveL).append("\n");

        if (libro.hayEspera()) {
            Usuario siguiente = libro.siguienteEnCola();
            libro.setDisponible(false);
            siguiente.agregarLibro(libro);
            String claveSig = GrafoBiblioteca.claveUsuario(siguiente.getId());
            grafo.eliminarAristaPorTipo(claveSig, claveL, TipoRelacion.ESPERA);
            grafo.agregarArista(claveSig, claveL, TipoRelacion.PRESTAMO_ACTIVO, 1);
            mensaje.append("  >> Asignado automaticamente a: ").append(siguiente.getNombre())
                   .append(" (cola FIFO -> PRESTAMO_ACTIVO en grafo).");
        } else {
            libro.setDisponible(true);
            mensaje.append("  >> El libro esta ahora DISPONIBLE.");
        }

        return mensaje.toString();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSULTAS DEL GRAFO
    // ═══════════════════════════════════════════════════════════════════════

    public String mostrarVecinos(int id, boolean esUsuario) {
        String clave = esUsuario
                ? GrafoBiblioteca.claveUsuario(id)
                : GrafoBiblioteca.claveLibro(id);

        if (!grafo.existeNodo(clave)) {
            return "  ERROR: Nodo " + clave + " no existe en el grafo.";
        }

        List<Arista> vecinos = grafo.obtenerVecinos(clave);
        StringBuilder sb = new StringBuilder();
        sb.append("  Vecinos salientes de ").append(clave).append(":\n");

        if (vecinos.isEmpty()) {
            sb.append("    (ninguna arista saliente)\n");
        } else {
            for (Arista a : vecinos) {
                sb.append("    ").append(a).append("\n");
            }
        }

        if (!esUsuario) {
            List<String> usuariosEntrantes = grafo.usuariosRelacionadosConLibro(id);
            if (!usuariosEntrantes.isEmpty()) {
                sb.append("  Usuarios conectados (aristas hacia este libro):\n");
                for (String u : usuariosEntrantes) {
                    Usuario usr = buscarUsuarioPorId(GrafoBiblioteca.extraerId(u));
                    String nombre = usr != null ? usr.getNombre() : u;
                    Arista a = grafo.buscarArista(u, clave);
                    if (a != null) {
                        sb.append("    ").append(u).append(" (").append(nombre).append(") ")
                          .append(a).append("\n");
                    }
                }
            }
        }
        return sb.toString().trim();
    }

    public String mostrarRecomendaciones(int idUsuario, int max) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "  ERROR: Usuario no encontrado.";

        Set<Integer> excluidos = new HashSet<>();
        for (Libro l : usuario.getLibrosPrestados()) {
            excluidos.add(l.getId());
        }

        List<GrafoBiblioteca.ResultadoRecomendacion> recs =
                grafo.recomendarLibros(idUsuario, max, excluidos);

        if (recs.isEmpty()) {
            return "  Sin recomendaciones (necesita mas interacciones en el grafo).\n"
                 + "  Prueba: que otros usuarios compartan libros con historial/prestamo.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("  Recomendaciones BFS para ").append(usuario.getNombre())
          .append(" (ID ").append(idUsuario).append("):\n");
        int pos = 1;
        for (GrafoBiblioteca.ResultadoRecomendacion r : recs) {
            Libro l = buscarLibroPorId(r.idLibro);
            String titulo = l != null ? l.getTitulo() : "ID " + r.idLibro;
            String disp = l != null && l.isDisponible() ? "DISPONIBLE" : "PRESTADO";
            sb.append("    ").append(pos++).append(". [ID ").append(r.idLibro).append("] ")
              .append(titulo).append(" | ").append(disp)
              .append(" | afinidad=").append(r.puntuacion).append("\n");
        }
        return sb.toString().trim();
    }

    public String visualizarGrafo() {
        return grafo.visualizar();
    }

    public String mostrarMetricas() {
        return grafo.mostrarMetricasEficiencia(
                registroUsuarios.size(), catalogoLibros.size());
    }

    public String mostrarHistorialDevoluciones() {
        if (historialDevoluciones.isEmpty()) {
            return "  (Sin devoluciones registradas aun)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = historialDevoluciones.size() - 1; i >= 0; i--) {
            sb.append("  ").append(historialDevoluciones.size() - i).append(". ")
              .append(historialDevoluciones.get(i).getTitulo())
              .append(" (").append(historialDevoluciones.get(i).getAutor()).append(")\n");
        }
        return sb.toString().trim();
    }

    public String ejecutarEscenarioPrueba() {
        StringBuilder sb = new StringBuilder();
        sb.append("  === ESCENARIO DE VALIDACION FASE 3 ===\n\n");

        sb.append("  [1] Nodos en grafo: ").append(grafo.contarNodos()).append("\n");
        sb.append("  [2] Aristas en grafo: ").append(grafo.contarAristas()).append("\n\n");

        sb.append("  [3] Vecinos de Ana (U:1):\n");
        sb.append(mostrarVecinos(1, true)).append("\n\n");

        sb.append("  [4] Recomendaciones para Ana (U:1):\n");
        sb.append(mostrarRecomendaciones(1, 5)).append("\n\n");

        sb.append("  [5] Prestamo: Carlos pide Cosmos (ID 2)...\n");
        sb.append("  ").append(realizarPrestamo(2, 2)).append("\n\n");

        sb.append("  [6] Recomendaciones para Maria (U:3) tras cambios:\n");
        sb.append(mostrarRecomendaciones(3, 5)).append("\n\n");

        sb.append("  [7] Visualizacion parcial del grafo:\n");
        sb.append(grafo.visualizar()).append("\n\n");

        sb.append("  [8] Metricas:\n");
        sb.append(mostrarMetricas()).append("\n");

        sb.append("\n  === FIN DEL ESCENARIO ===");
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CATEGORIAS Y ACCESORES
    // ═══════════════════════════════════════════════════════════════════════

    public boolean categoriaValida(String categoria) {
        for (String cat : CATEGORIAS) {
            if (cat.equalsIgnoreCase(categoria)) return true;
        }
        return false;
    }

    public String[] getCategorias()              { return CATEGORIAS; }
    public List<Libro> getCatalogoLibros()       { return catalogoLibros; }
    public List<Usuario> getRegistroUsuarios()   { return registroUsuarios; }

    // ═══════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ═══════════════════════════════════════════════════════════════════════

    private void cargarDatosDemo() {
        registrarLibro("El Resplandor",              "Stephen King",      "Terror");
        registrarLibro("Cosmos",                     "Carl Sagan",        "Ciencia");
        registrarLibro("Sapiens",                    "Yuval Noah Harari", "Historia");
        registrarLibro("El Senor de los Anillos",    "J.R.R. Tolkien",    "Fantasia");
        registrarLibro("Breve Historia del Tiempo",  "Stephen Hawking",   "Ciencia");
        registrarLibro("1984",                       "George Orwell",     "Historia");
        registrarLibro("Dune",                       "Frank Herbert",     "Fantasia");

        registrarUsuario("Ana Garcia");
        registrarUsuario("Carlos Lopez");
        registrarUsuario("Maria Perez");

        // Prestamos precargados para demostrar grafo y recomendaciones
        Libro cosmos = buscarLibroPorId(2);
        Libro sapiens = buscarLibroPorId(3);
        Libro dune = buscarLibroPorId(7);

        Usuario ana = buscarUsuarioPorId(1);
        Usuario carlos = buscarUsuarioPorId(2);
        Usuario maria = buscarUsuarioPorId(3);

        // Ana tiene Cosmos
        cosmos.setDisponible(false);
        ana.agregarLibro(cosmos);
        grafo.agregarArista(
                GrafoBiblioteca.claveUsuario(1), GrafoBiblioteca.claveLibro(2),
                TipoRelacion.PRESTAMO_ACTIVO, 1);

        // Carlos tiene historial con Sapiens (devuelto simulado)
        grafo.agregarArista(
                GrafoBiblioteca.claveUsuario(2), GrafoBiblioteca.claveLibro(3),
                TipoRelacion.HISTORIAL, 2);

        // Carlos tambien tiene Cosmos en historial (afinidad con Ana)
        grafo.agregarArista(
                GrafoBiblioteca.claveUsuario(2), GrafoBiblioteca.claveLibro(2),
                TipoRelacion.HISTORIAL, 1);

        // Maria en espera por Dune (prestado a Carlos ficticio para demo)
        dune.setDisponible(false);
        carlos.agregarLibro(dune);
        grafo.agregarArista(
                GrafoBiblioteca.claveUsuario(2), GrafoBiblioteca.claveLibro(7),
                TipoRelacion.PRESTAMO_ACTIVO, 1);
        dune.agregarAColaEspera(maria);
        grafo.agregarArista(
                GrafoBiblioteca.claveUsuario(3), GrafoBiblioteca.claveLibro(7),
                TipoRelacion.ESPERA, 1);

        // Resplandor disponible - sin arista activa
    }
}
