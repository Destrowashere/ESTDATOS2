package biblioteca;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Binario de Búsqueda (BST) para el catálogo de libros.
 *
 * Ordenado por ID de libro:
 *   - ID menor  → subárbol izquierdo
 *   - ID mayor  → subárbol derecho
 *
 * Ventaja frente al ArrayList anterior:
 *   - ArrayList:  búsqueda O(n)     — recorre todos los elementos
 *   - BST:        búsqueda O(log n) — descarta la mitad en cada paso
 */
public class ArbolLibros {

    private NodoLibro raiz;
    private int totalNodos;

    public ArbolLibros() {
        this.raiz = null;
        this.totalNodos = 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  INSERCIÓN  O(log n) promedio
    // ═══════════════════════════════════════════════════════════════════════

    public void insertar(Libro libro) {
        raiz = insertarRecursivo(raiz, libro);
        totalNodos++;
    }

    private NodoLibro insertarRecursivo(NodoLibro nodo, Libro libro) {
        if (nodo == null) {
            return new NodoLibro(libro);
        }
        if (libro.getId() < nodo.libro.getId()) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, libro);
        } else if (libro.getId() > nodo.libro.getId()) {
            nodo.derecho = insertarRecursivo(nodo.derecho, libro);
        }
        // ID duplicado: no se inserta (libro ya existe)
        return nodo;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BÚSQUEDA  O(log n) promedio
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Busca un libro por ID descartando la mitad del árbol en cada paso.
     * @return el Libro encontrado, o null si no existe.
     */
    public Libro buscar(int id) {
        return buscarRecursivo(raiz, id);
    }

    private Libro buscarRecursivo(NodoLibro nodo, int id) {
        if (nodo == null) return null;           // No encontrado
        if (id == nodo.libro.getId()) return nodo.libro;
        if (id < nodo.libro.getId())
            return buscarRecursivo(nodo.izquierdo, id);   // Ir a la izquierda
        else
            return buscarRecursivo(nodo.derecho, id);     // Ir a la derecha
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  RECORRIDO INORDEN (izquierda → raíz → derecha)  O(n)
    //  Produce los libros ordenados de menor a mayor ID — "gratis" con BST
    // ═══════════════════════════════════════════════════════════════════════

    public List<Libro> recorrerInorden() {
        List<Libro> resultado = new ArrayList<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void inordenRecursivo(NodoLibro nodo, List<Libro> lista) {
        if (nodo == null) return;
        inordenRecursivo(nodo.izquierdo, lista);
        lista.add(nodo.libro);
        inordenRecursivo(nodo.derecho, lista);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ELIMINACIÓN  O(log n) promedio
    // ═══════════════════════════════════════════════════════════════════════

    public boolean eliminar(int id) {
        if (buscar(id) == null) return false;
        raiz = eliminarRecursivo(raiz, id);
        totalNodos--;
        return true;
    }

    private NodoLibro eliminarRecursivo(NodoLibro nodo, int id) {
        if (nodo == null) return null;

        if (id < nodo.libro.getId()) {
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, id);
        } else if (id > nodo.libro.getId()) {
            nodo.derecho = eliminarRecursivo(nodo.derecho, id);
        } else {
            // Nodo encontrado — tres casos:
            if (nodo.izquierdo == null) return nodo.derecho;  // Sin hijo izq.
            if (nodo.derecho == null)   return nodo.izquierdo; // Sin hijo der.
            // Dos hijos: reemplazar con el sucesor inorden (mínimo del subárbol derecho)
            NodoLibro sucesor = minimoNodo(nodo.derecho);
            nodo.libro = sucesor.libro;
            nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.libro.getId());
        }
        return nodo;
    }

    private NodoLibro minimoNodo(NodoLibro nodo) {
        while (nodo.izquierdo != null) nodo = nodo.izquierdo;
        return nodo;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ═══════════════════════════════════════════════════════════════════════

    public boolean estaVacio()     { return raiz == null; }
    public int getTotalNodos()     { return totalNodos; }

    /**
     * Genera una representación visual del árbol en consola.
     * Muestra la estructura jerárquica con líneas de conexión.
     */
    public String visualizarArbol() {
        if (raiz == null) return "  (Arbol vacio)";
        StringBuilder sb = new StringBuilder();
        visualizarRecursivo(raiz, sb, "", "");
        return sb.toString();
    }

    private void visualizarRecursivo(NodoLibro nodo, StringBuilder sb,
                                     String prefijo, String conector) {
        if (nodo == null) return;
        sb.append(prefijo).append(conector)
          .append("[ID:").append(nodo.libro.getId()).append("] ")
          .append(nodo.libro.getTitulo())
          .append(nodo.libro.isDisponible() ? "" : " (PRESTADO)")
          .append("\n");

        String nuevoPrefijo = prefijo + (conector.equals("└── ") ? "    " : "│   ");
        if (nodo.izquierdo != null || nodo.derecho != null) {
            visualizarRecursivo(nodo.derecho,   sb, nuevoPrefijo, "├── ");
            visualizarRecursivo(nodo.izquierdo, sb, nuevoPrefijo, "└── ");
        }
    }

    /**
     * Calcula la altura del árbol para mostrar en documentación.
     */
    public int altura() {
        return alturaRecursiva(raiz);
    }

    private int alturaRecursiva(NodoLibro nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(alturaRecursiva(nodo.izquierdo),
                            alturaRecursiva(nodo.derecho));
    }
}
