package biblioteca;

/**
 * Nodo del Árbol Binario de Búsqueda (BST).
 * Cada nodo almacena un Libro y referencias a sus hijos izquierdo y derecho.
 * El árbol se ordena por el ID del libro: menores a la izquierda, mayores a la derecha.
 */
public class NodoLibro {

    Libro libro;
    NodoLibro izquierdo;
    NodoLibro derecho;

    public NodoLibro(Libro libro) {
        this.libro = libro;
        this.izquierdo = null;
        this.derecho = null;
    }
}
