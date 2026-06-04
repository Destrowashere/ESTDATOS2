package biblioteca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Grafo bipartito dirigido y ponderado: usuarios y libros como nodos,
 * interacciones (prestamo, espera, historial) como aristas.
 *
 * Representacion: lista de adyacencia con HashMap para acceso O(1) por clave.
 */
public class GrafoBiblioteca {

    private final Map<String, TipoNodo> nodos;
    private final Map<String, List<Arista>> adyacencia;

    public GrafoBiblioteca() {
        this.nodos = new HashMap<>();
        this.adyacencia = new HashMap<>();
    }

    // ── Claves de nodo ───────────────────────────────────────────────────────

    public static String claveUsuario(int id) {
        return "U:" + id;
    }

    public static String claveLibro(int id) {
        return "L:" + id;
    }

    public static boolean esClaveUsuario(String clave) {
        return clave != null && clave.startsWith("U:");
    }

    public static boolean esClaveLibro(String clave) {
        return clave != null && clave.startsWith("L:");
    }

    public static int extraerId(String clave) {
        return Integer.parseInt(clave.substring(2));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  NODOS
    // ═══════════════════════════════════════════════════════════════════════

    public void agregarNodoUsuario(int id) {
        agregarNodo(claveUsuario(id), TipoNodo.USUARIO);
    }

    public void agregarNodoLibro(int id) {
        agregarNodo(claveLibro(id), TipoNodo.LIBRO);
    }

    private void agregarNodo(String clave, TipoNodo tipo) {
        if (!nodos.containsKey(clave)) {
            nodos.put(clave, tipo);
            adyacencia.put(clave, new ArrayList<>());
        }
    }

    public boolean existeNodo(String clave) {
        return nodos.containsKey(clave);
    }

    public TipoNodo getTipoNodo(String clave) {
        return nodos.get(clave);
    }

    /**
     * Elimina el nodo y todas las aristas incidentes (entrantes y salientes).
     */
    public boolean eliminarNodo(String clave) {
        if (!existeNodo(clave)) return false;

        adyacencia.remove(clave);
        nodos.remove(clave);

        for (String origen : new ArrayList<>(adyacencia.keySet())) {
            List<Arista> lista = adyacencia.get(origen);
            lista.removeIf(a -> a.getDestino().equals(clave));
        }
        return true;
    }

    public int contarNodos() {
        return nodos.size();
    }

    public Set<String> getClavesNodos() {
        return new HashSet<>(nodos.keySet());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ARISTAS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Agrega o actualiza arista. Valida bipartito: solo U->L o L->U.
     */
    public boolean agregarArista(String origen, String destino, TipoRelacion tipo, int peso) {
        if (!existeNodo(origen) || !existeNodo(destino)) return false;
        if (!esBipartitoValido(origen, destino)) return false;
        if (peso < 1) peso = 1;

        Arista existente = buscarArista(origen, destino);
        if (existente != null) {
            existente.setTipo(tipo);
            existente.setPeso(peso);
            return true;
        }

        adyacencia.get(origen).add(new Arista(destino, peso, tipo));
        return true;
    }

    private boolean esBipartitoValido(String origen, String destino) {
        TipoNodo tOrigen = nodos.get(origen);
        TipoNodo tDestino = nodos.get(destino);
        return tOrigen != tDestino;
    }

    public Arista buscarArista(String origen, String destino) {
        if (!adyacencia.containsKey(origen)) return null;
        for (Arista a : adyacencia.get(origen)) {
            if (a.getDestino().equals(destino)) return a;
        }
        return null;
    }

    public boolean eliminarArista(String origen, String destino) {
        if (!adyacencia.containsKey(origen)) return false;
        return adyacencia.get(origen).removeIf(a -> a.getDestino().equals(destino));
    }

    public boolean eliminarAristaPorTipo(String origen, String destino, TipoRelacion tipo) {
        if (!adyacencia.containsKey(origen)) return false;
        return adyacencia.get(origen).removeIf(
                a -> a.getDestino().equals(destino) && a.getTipo() == tipo);
    }

    public boolean actualizarPeso(String origen, String destino, int nuevoPeso) {
        Arista a = buscarArista(origen, destino);
        if (a == null) return false;
        a.setPeso(nuevoPeso);
        return true;
    }

    public List<Arista> obtenerVecinos(String clave) {
        if (!adyacencia.containsKey(clave)) return new ArrayList<>();
        return new ArrayList<>(adyacencia.get(clave));
    }

    public List<Arista> obtenerVecinosPorTipo(String clave, TipoRelacion tipo) {
        List<Arista> filtrados = new ArrayList<>();
        for (Arista a : obtenerVecinos(clave)) {
            if (a.getTipo() == tipo) filtrados.add(a);
        }
        return filtrados;
    }

    /** Aristas entrantes hacia un nodo (busqueda en todo el grafo). */
    public List<Arista> obtenerAristasEntrantes(String destino) {
        List<Arista> entrantes = new ArrayList<>();
        for (Map.Entry<String, List<Arista>> entry : adyacencia.entrySet()) {
            for (Arista a : entry.getValue()) {
                if (a.getDestino().equals(destino)) {
                    entrantes.add(a);
                }
            }
        }
        return entrantes;
    }

    public int contarAristas() {
        int total = 0;
        for (List<Arista> lista : adyacencia.values()) {
            total += lista.size();
        }
        return total;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSULTAS AVANZADAS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Usuarios conectados a un libro (aristas salientes desde L o entrantes a L).
     */
    public List<String> usuariosRelacionadosConLibro(int idLibro) {
        String claveLibro = claveLibro(idLibro);
        Set<String> usuarios = new HashSet<>();

        for (Arista a : obtenerVecinos(claveLibro)) {
            if (esClaveUsuario(a.getDestino())) usuarios.add(a.getDestino());
        }
        for (String clave : nodos.keySet()) {
            if (!esClaveUsuario(clave)) continue;
            for (Arista a : obtenerVecinos(clave)) {
                if (a.getDestino().equals(claveLibro)) usuarios.add(clave);
            }
        }
        return new ArrayList<>(usuarios);
    }

    /**
     * BFS bipartito (profundidad 4): U -> L -> U -> L.
     * Recomienda libros que usuarios afines tomaron, ponderado por peso de aristas.
     */
    public List<ResultadoRecomendacion> recomendarLibros(
            int idUsuario, int maxResultados, Set<Integer> librosExcluidos) {

        String inicio = claveUsuario(idUsuario);
        List<ResultadoRecomendacion> resultados = new ArrayList<>();
        if (!existeNodo(inicio) || maxResultados <= 0) return resultados;

        Map<String, Integer> puntuacionLibros = new HashMap<>();

        Queue<String> cola = new LinkedList<>();
        Map<String, Integer> profundidad = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        cola.offer(inicio);
        profundidad.put(inicio, 0);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            int prof = profundidad.get(actual);

            if (prof >= 4) continue;

            for (Arista arista : obtenerVecinos(actual)) {
                String vecino = arista.getDestino();
                int nuevaProf = prof + 1;

                if (esClaveLibro(vecino) && nuevaProf <= 4) {
                    int idLib = extraerId(vecino);
                    if (!librosExcluidos.contains(idLib) && nuevaProf == 2) {
                        puntuacionLibros.merge(vecino, arista.getPeso(), Integer::sum);
                    } else if (nuevaProf == 4 && !librosExcluidos.contains(idLib)) {
                        puntuacionLibros.merge(vecino, arista.getPeso(), Integer::sum);
                    }
                }

                if (!visitados.contains(vecino) && nuevaProf < 4) {
                    visitados.add(vecino);
                    profundidad.put(vecino, nuevaProf);
                    cola.offer(vecino);
                }
            }
        }

        // Segunda pasada: caminos U(0) -> L(1) -> U(2) -> L(3) con puntuacion acumulada
        puntuacionLibros.clear();
        for (Arista a1 : obtenerVecinos(inicio)) {
            if (!esClaveLibro(a1.getDestino())) continue;
            String claveL1 = a1.getDestino();

            for (String claveU2 : nodos.keySet()) {
                if (!esClaveUsuario(claveU2) || claveU2.equals(inicio)) continue;
                Arista a2 = buscarArista(claveU2, claveL1);
                if (a2 == null) continue;

                for (Arista a3 : obtenerVecinos(claveU2)) {
                    if (!esClaveLibro(a3.getDestino()) || a3.getDestino().equals(claveL1)) continue;
                    String claveL2 = a3.getDestino();
                    int idL2 = extraerId(claveL2);
                    if (librosExcluidos.contains(idL2)) continue;

                    int puntos = a1.getPeso() + a2.getPeso() + a3.getPeso();
                    puntuacionLibros.merge(claveL2, puntos, Integer::sum);
                }
            }
        }

        puntuacionLibros.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(maxResultados)
                .forEach(e -> resultados.add(
                        new ResultadoRecomendacion(extraerId(e.getKey()), e.getValue())));

        return resultados;
    }

    public static class ResultadoRecomendacion {
        public final int idLibro;
        public final int puntuacion;

        public ResultadoRecomendacion(int idLibro, int puntuacion) {
            this.idLibro = idLibro;
            this.puntuacion = puntuacion;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VISUALIZACION Y METRICAS
    // ═══════════════════════════════════════════════════════════════════════

    public String visualizar() {
        if (nodos.isEmpty()) return "  (Grafo vacio - sin nodos)";

        StringBuilder sb = new StringBuilder();
        sb.append("  --- LISTA DE ADYACENCIA (Grafo Bipartito Dirigido) ---\n");
        List<String> claves = new ArrayList<>(nodos.keySet());
        claves.sort(String::compareTo);

        for (String clave : claves) {
            sb.append("  ").append(clave).append(" [").append(nodos.get(clave)).append("]\n");
            List<Arista> aristas = adyacencia.get(clave);
            if (aristas.isEmpty()) {
                sb.append("    (sin aristas salientes)\n");
            } else {
                for (Arista a : aristas) {
                    sb.append("    ").append(a).append("\n");
                }
            }
        }
        return sb.toString().trim();
    }

    public String mostrarMetricasEficiencia(int totalUsuarios, int totalLibros) {
        int aristas = contarAristas();
        int nodos = contarNodos();
        double gradoPromedio = nodos > 0 ? (double) aristas / nodos : 0;

        return String.format(
            "  Nodos en el grafo (V)     : %d\n" +
            "  Aristas dirigidas (E)     : %d\n" +
            "  Grado saliente promedio   : %.2f\n" +
            "  Usuarios en sistema       : %d\n" +
            "  Libros en catalogo        : %d\n" +
            "  Comparacion de consultas:\n" +
            "    Lista lineal (Fase 1)   : O(U x L) = hasta %d operaciones\n" +
            "    Grafo por vecinos       : O(grado) ~ O(%d) por usuario tipico\n" +
            "    BFS recomendaciones     : O(V + E) = O(%d)\n" +
            "  Representacion: lista de adyacencia (dispersa, no matriz V^2)",
            nodos, aristas, gradoPromedio,
            totalUsuarios, totalLibros,
            totalUsuarios * totalLibros,
            (int) Math.ceil(gradoPromedio),
            nodos + aristas
        );
    }
}
