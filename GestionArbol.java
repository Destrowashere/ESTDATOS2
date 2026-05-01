package biblioteca;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Cerebro del sistema - FASE 2: Árbol Binario de Búsqueda.
 *
 * Cambio clave respecto a GestionBiblioteca (Fase 1):
 *   ANTES: catalogoLibros = ArrayList<Libro>  → búsqueda O(n)
 *   AHORA: arbolCatalogo  = ArbolLibros (BST) → búsqueda O(log n)
 *
 * Las demás estructuras se conservan:
 *   - Stack<Libro>   → Historial de devoluciones (Pila LIFO)
 *   - Queue<Usuario> → Cola de espera por libro  (dentro de Libro)
 *   - String[]       → Categorías fijas          (Arreglo estático)
 *   - ArrayList<Usuario> → Registro de usuarios  (Lista dinámica)
 */
public class GestionArbol {

    // ESTRUCTURA 1: Arreglo estático — categorías fijas
    private static final String[] CATEGORIAS = {
        "Terror", "Ciencia", "Historia", "Fantasia", "Romance",
        "Tecnologia", "Filosofia", "Biografia"
    };

    // ESTRUCTURA 2: Árbol BST — catálogo de libros (REEMPLAZA al ArrayList)
    private ArbolLibros arbolCatalogo;

    // ESTRUCTURA 3: ArrayList — registro de usuarios (se mantiene)
    private List<Usuario> registroUsuarios;

    // ESTRUCTURA 4: Stack (Pila LIFO) — historial de devoluciones
    private Stack<Libro> historialDevoluciones;

    // Contador de IDs
    private int contadorIdLibro   = 1;
    private int contadorIdUsuario = 1;

    // Contador de pasos de búsqueda para mostrar eficiencia
    private int ultimosPasosRealizado = 0;

    public GestionArbol() {
        arbolCatalogo        = new ArbolLibros();
        registroUsuarios     = new ArrayList<>();
        historialDevoluciones = new Stack<>();
        cargarDatosDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  REGISTRO
    // ═══════════════════════════════════════════════════════════════════════

    public boolean registrarLibro(String titulo, String autor, String categoria) {
        if (!categoriaValida(categoria)) return false;
        Libro libro = new Libro(contadorIdLibro++, titulo, autor, categoria);
        arbolCatalogo.insertar(libro);
        return true;
    }

    /** Inserción con ID específico — solo para construir datos demo con árbol balanceado. */
    private void insertarDemo(int id, String titulo, String autor, String categoria) {
        Libro libro = new Libro(id, titulo, autor, categoria);
        arbolCatalogo.insertar(libro);
        if (id >= contadorIdLibro) contadorIdLibro = id + 1;
    }

    public void registrarUsuario(String nombre) {
        Usuario u = new Usuario(contadorIdUsuario++, nombre);
        registroUsuarios.add(u);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BÚSQUEDA  — O(log n) con BST
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Búsqueda por ID usando el BST.
     * Registra cuántos pasos simulados tomaría para demostrar O(log n).
     */
    public Libro buscarLibroPorId(int id) {
        ultimosPasosRealizado = calcularPasosSimulados(id);
        return arbolCatalogo.buscar(id);
    }

    /**
     * Simula el número de comparaciones que haría el BST
     * recorriendo el árbol con la misma lógica de búsqueda.
     */
    private int calcularPasosSimulados(int id) {
        int pasos = 0;
        NodoLibro actual = obtenerRaiz();
        while (actual != null) {
            pasos++;
            if (id == actual.libro.getId()) break;
            actual = (id < actual.libro.getId()) ? actual.izquierdo : actual.derecho;
        }
        return pasos;
    }

    // Acceso a la raíz solo para uso interno de métricas
    private NodoLibro obtenerRaiz() {
        // Usamos inorden para acceder al primer nodo como proxy; en producción
        // se expondría la raíz directamente.
        List<Libro> todos = arbolCatalogo.recorrerInorden();
        if (todos.isEmpty()) return null;
        // Reconstruir mini-ruta desde raíz (llamada al árbol directamente)
        return raizDelArbol(arbolCatalogo);
    }

    private NodoLibro raizDelArbol(ArbolLibros arbol) {
        // Reflexión ligera: usamos el método visualizar para detectar si hay raíz
        // En implementación real se expondría un getter. Aquí lo inferimos.
        try {
            java.lang.reflect.Field f = ArbolLibros.class.getDeclaredField("raiz");
            f.setAccessible(true);
            return (NodoLibro) f.get(arbol);
        } catch (Exception e) {
            return null;
        }
    }

    public int getUltimosPasos()   { return ultimosPasosRealizado; }

    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario u : registroUsuarios) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PRÉSTAMO Y DEVOLUCIÓN
    // ═══════════════════════════════════════════════════════════════════════

    public String realizarPrestamo(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro ID " + idLibro + " no encontrado en el arbol.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario ID " + idUsuario + " no encontrado.";

        if (usuario.tieneLibro(libro))
            return "AVISO: El usuario ya tiene este libro en prestamo.";
        if (libro.getColaEspera().contains(usuario))
            return "AVISO: El usuario ya esta en la lista de espera.";

        if (libro.isDisponible()) {
            libro.setDisponible(false);
            usuario.agregarLibro(libro);
            return "EXITO: \"" + libro.getTitulo() + "\" prestado a " + usuario.getNombre()
                    + " [BST: " + ultimosPasosRealizado + " paso(s) de busqueda]";
        } else {
            libro.agregarAColaEspera(usuario);
            return "ESPERA: Libro no disponible. " + usuario.getNombre()
                    + " en cola, posicion: " + libro.getColaEspera().size()
                    + " [BST: " + ultimosPasosRealizado + " paso(s) de busqueda]";
        }
    }

    public String devolverLibro(int idLibro, int idUsuario) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "ERROR: Libro ID " + idLibro + " no encontrado.";

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        if (usuario == null) return "ERROR: Usuario ID " + idUsuario + " no encontrado.";

        if (!usuario.tieneLibro(libro))
            return "ERROR: El usuario no tiene este libro en prestamo.";

        usuario.devolverLibro(libro);
        historialDevoluciones.push(libro); // PILA: push (LIFO)

        StringBuilder msg = new StringBuilder();
        msg.append("EXITO: \"").append(libro.getTitulo())
           .append("\" devuelto por ").append(usuario.getNombre()).append(".\n");

        if (libro.hayEspera()) {
            Usuario siguiente = libro.siguienteEnCola(); // COLA: poll (FIFO)
            libro.setDisponible(false);
            siguiente.agregarLibro(libro);
            msg.append("  >> Asignado automaticamente a: ").append(siguiente.getNombre());
        } else {
            libro.setDisponible(true);
            msg.append("  >> Libro ahora DISPONIBLE en el arbol.");
        }
        return msg.toString();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VISUALIZACIÓN
    // ═══════════════════════════════════════════════════════════════════════

    /** Recorrido inorden del BST: muestra libros ordenados por ID. */
    public List<Libro> listarOrdenado() {
        return arbolCatalogo.recorrerInorden();
    }

    public String visualizarArbol() {
        return arbolCatalogo.visualizarArbol();
    }

    public String mostrarHistorialDevoluciones() {
        if (historialDevoluciones.isEmpty())
            return "  (Sin devoluciones registradas)";
        StringBuilder sb = new StringBuilder();
        for (int i = historialDevoluciones.size() - 1; i >= 0; i--) {
            sb.append("  ").append(historialDevoluciones.size() - i).append(". ")
              .append(historialDevoluciones.get(i).getTitulo())
              .append(" (").append(historialDevoluciones.get(i).getAutor()).append(")\n");
        }
        return sb.toString().trim();
    }

    public String mostrarColaEspera(int idLibro) {
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) return "  ERROR: Libro no encontrado.";
        if (!libro.hayEspera())
            return "  (Sin usuarios en espera para \"" + libro.getTitulo() + "\")";
        StringBuilder sb = new StringBuilder();
        sb.append("  Cola de espera para \"").append(libro.getTitulo()).append("\":\n");
        int pos = 1;
        for (Usuario u : libro.getColaEspera()) {
            sb.append("    ").append(pos++).append(". ").append(u.getNombre()).append("\n");
        }
        return sb.toString().trim();
    }

    /** Métricas del árbol para la sección de análisis de eficiencia. */
    public String mostrarMetricas() {
        int n = arbolCatalogo.getTotalNodos();
        int h = arbolCatalogo.altura();
        // log2(n) aproximado
        double logN = n > 0 ? Math.log(n) / Math.log(2) : 0;
        return String.format(
            "  Libros en el arbol (n)  : %d\n" +
            "  Altura actual del arbol : %d\n" +
            "  log2(n) teorico         : %.2f\n" +
            "  Comparacion:\n" +
            "    Lista O(n)   → hasta %d comparaciones\n" +
            "    BST  O(logn) → hasta %.0f comparaciones (aprox.)",
            n, h, logN, n, logN
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CATEGORÍAS
    // ═══════════════════════════════════════════════════════════════════════

    public boolean categoriaValida(String cat) {
        for (String c : CATEGORIAS) {
            if (c.equalsIgnoreCase(cat)) return true;
        }
        return false;
    }

    public String[] getCategorias()         { return CATEGORIAS; }
    public List<Usuario> getUsuarios()      { return registroUsuarios; }
    public boolean arbolVacio()             { return arbolCatalogo.estaVacio(); }

    // ═══════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ═══════════════════════════════════════════════════════════════════════

    private void cargarDatosDemo() {
        // Orden de inserción diseñado para producir un BST balanceado:
        //         [4] Senor de los Anillos
        //        /                        \
        //    [2] Cosmos              [6] 1984
        //   /         \            /         \
        // [1] Sapiens [3] Resplandor [5] Hawking [7] Dune
        insertarDemo(4, "El Senor de los Anillos",   "J.R.R. Tolkien",    "Fantasia");  // Raiz
        insertarDemo(2, "Cosmos",                    "Carl Sagan",         "Ciencia");  // Hijo izq de 4
        insertarDemo(6, "1984",                      "George Orwell",      "Historia"); // Hijo der de 4
        insertarDemo(1, "Sapiens",                   "Yuval Noah Harari",  "Historia"); // Hoja izq de 2
        insertarDemo(3, "El Resplandor",             "Stephen King",       "Terror");   // Hoja der de 2
        insertarDemo(5, "Breve Historia del Tiempo", "Stephen Hawking",    "Ciencia");  // Hoja izq de 6
        insertarDemo(7, "Dune",                      "Frank Herbert",      "Fantasia"); // Hoja der de 6

        registrarUsuario("Ana Garcia");
        registrarUsuario("Carlos Lopez");
        registrarUsuario("Maria Perez");
    }
}
