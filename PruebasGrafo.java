package biblioteca;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pruebas automaticas de la estructura GrafoBiblioteca (Fase 3).
 * Ejecutar: java biblioteca.PruebasGrafo
 */
public class PruebasGrafo {

    private static int pasadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("\n  ===== PRUEBAS GRAFO BIBLIOTECA - FASE 3 =====\n");

        prueba1_insertarNodos();
        prueba2_aristaPrestamo();
        prueba3_eliminarArista();
        prueba4_recomendacionesBfs();
        prueba5_eliminarNodoSinHuérfanas();

        System.out.println("\n  ----------------------------------------");
        System.out.println("  Resultado: " + pasadas + " pasaron, " + fallidas + " fallaron");
        System.out.println(fallidas == 0 ? "  ESTADO: TODAS OK\n" : "  ESTADO: HAY FALLOS\n");
    }

    private static void prueba1_insertarNodos() {
        GrafoBiblioteca g = new GrafoBiblioteca();
        g.agregarNodoUsuario(1);
        g.agregarNodoLibro(1);
        assertTrue("P1: existe U:1", g.existeNodo(GrafoBiblioteca.claveUsuario(1)));
        assertTrue("P1: existe L:1", g.existeNodo(GrafoBiblioteca.claveLibro(1)));
        assertEquals("P1: 2 nodos", 2, g.contarNodos());
    }

    private static void prueba2_aristaPrestamo() {
        GrafoBiblioteca g = new GrafoBiblioteca();
        g.agregarNodoUsuario(1);
        g.agregarNodoLibro(1);
        g.agregarArista(
                GrafoBiblioteca.claveUsuario(1),
                GrafoBiblioteca.claveLibro(1),
                TipoRelacion.PRESTAMO_ACTIVO, 1);

        List<Arista> vecinos = g.obtenerVecinos(GrafoBiblioteca.claveUsuario(1));
        assertTrue("P2: vecino L:1 presente", vecinos.size() == 1
                && vecinos.get(0).getDestino().equals(GrafoBiblioteca.claveLibro(1)));
        assertEquals("P2: 1 arista", 1, g.contarAristas());
    }

    private static void prueba3_eliminarArista() {
        GrafoBiblioteca g = new GrafoBiblioteca();
        g.agregarNodoUsuario(1);
        g.agregarNodoLibro(1);
        String u = GrafoBiblioteca.claveUsuario(1);
        String l = GrafoBiblioteca.claveLibro(1);
        g.agregarArista(u, l, TipoRelacion.PRESTAMO_ACTIVO, 1);
        g.eliminarArista(u, l);
        assertTrue("P3: sin vecinos tras eliminar", g.obtenerVecinos(u).isEmpty());
    }

    private static void prueba4_recomendacionesBfs() {
        GrafoBiblioteca g = new GrafoBiblioteca();
        for (int i = 1; i <= 3; i++) g.agregarNodoUsuario(i);
        for (int i = 1; i <= 3; i++) g.agregarNodoLibro(i);

        // U1 -> L1 (historial)
        g.agregarArista("U:1", "L:1", TipoRelacion.HISTORIAL, 2);
        // U2 -> L1 y U2 -> L2
        g.agregarArista("U:2", "L:1", TipoRelacion.HISTORIAL, 1);
        g.agregarArista("U:2", "L:2", TipoRelacion.PRESTAMO_ACTIVO, 1);

        Set<Integer> excluidos = new HashSet<>();
        excluidos.add(1);
        List<GrafoBiblioteca.ResultadoRecomendacion> recs =
                g.recomendarLibros(1, 5, excluidos);

        boolean tieneL2 = false;
        for (GrafoBiblioteca.ResultadoRecomendacion r : recs) {
            if (r.idLibro == 2) tieneL2 = true;
        }
        assertTrue("P4: U1 recibe recomendacion L2", tieneL2);
    }

    private static void prueba5_eliminarNodoSinHuérfanas() {
        GrafoBiblioteca g = new GrafoBiblioteca();
        g.agregarNodoUsuario(1);
        g.agregarNodoLibro(1);
        g.agregarNodoLibro(2);
        g.agregarArista("U:1", "L:1", TipoRelacion.PRESTAMO_ACTIVO, 1);
        g.agregarArista("U:1", "L:2", TipoRelacion.ESPERA, 1);
        g.eliminarNodo("U:1");

        assertTrue("P5: U:1 eliminado", !g.existeNodo("U:1"));
        assertTrue("P5: sin aristas a U:1 en L:1", g.buscarArista("L:1", "U:1") == null);

        for (String clave : g.getClavesNodos()) {
            for (Arista a : g.obtenerVecinos(clave)) {
                assertTrue("P5: no referencia U:1", !a.getDestino().equals("U:1"));
            }
        }
    }

    private static void assertTrue(String nombre, boolean condicion) {
        if (condicion) {
            pasadas++;
            System.out.println("  [OK] " + nombre);
        } else {
            fallidas++;
            System.out.println("  [FALLO] " + nombre);
        }
    }

    private static void assertEquals(String nombre, int esperado, int actual) {
        assertTrue(nombre, esperado == actual);
    }
}
