package biblioteca;

import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Gestión de Biblioteca.
 *
 * FASE 1: Estructuras lineales (ArrayList, Queue, Stack, Array)
 * FASE 2: Árbol Binario de Búsqueda (BST) como catálogo principal
 */
public class Main {

    private static final Scanner scanner     = new Scanner(System.in);
    private static final GestionBiblioteca gestion      = new GestionBiblioteca();
    private static final GestionArbol      gestionArbol = new GestionArbol();

    public static void main(String[] args) {
        System.out.println("\n  Sistema iniciado. Datos demo cargados en ambas fases.\n");

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opcion: ");
            switch (opcion) {
                case 1: menuRegistrar();   break;
                case 2: menuBuscar();      break;
                case 3: menuPrestamo();    break;
                case 4: menuDevolucion();  break;
                case 5: menuHistorial();   break;
                case 6: menuColaEspera();  break;
                case 7: menuFase2();       break;
                case 8: break;
                default:
                    System.out.println("  Opcion invalida.");
            }
        } while (opcion != 8);

        scanner.close();
        System.out.println("\n  Hasta luego. Sesion cerrada.\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ═══════════════════════════════════════════════════════════════════════

    private static void mostrarMenuPrincipal() {
        System.out.println("\n************************************************************");
        System.out.println("         SISTEMA DE GESTION DE BIBLIOTECA");
        System.out.println("************************************************************");
        System.out.println("  --- FASE 1: Estructuras Lineales ---");
        System.out.println("  1. Registrar Libro / Usuario");
        System.out.println("  2. Buscar Libro (por Titulo o ID)");
        System.out.println("  3. Realizar Prestamo");
        System.out.println("  4. Devolver Libro");
        System.out.println("  5. Ver Historial de Devoluciones (Pila LIFO)");
        System.out.println("  6. Ver Lista de Espera de un Libro (Cola FIFO)");
        System.out.println("  --------------------------------------------------------");
        System.out.println("  7. >> Ir a FASE 2: Arbol Binario de Busqueda (BST) <<");
        System.out.println("  8. Salir");
        System.out.println("------------------------------------------------------------");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 1: REGISTRAR
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuRegistrar() {
        System.out.println("\n  Que desea registrar?");
        System.out.println("    1. Libro");
        System.out.println("    2. Usuario");
        int sub = leerEntero("  Opcion: ");

        if (sub == 1) {
            System.out.println("\n  Categorias disponibles:");
            String[] cats = gestion.getCategorias();
            for (int i = 0; i < cats.length; i++) {
                System.out.println("    " + (i + 1) + ". " + cats[i]);
            }
            System.out.print("  Titulo    : ");
            String titulo = scanner.nextLine().trim();
            System.out.print("  Autor     : ");
            String autor = scanner.nextLine().trim();
            System.out.print("  Categoria : ");
            String categoria = scanner.nextLine().trim();

            boolean ok = gestion.registrarLibro(titulo, autor, categoria);
            if (ok) {
                System.out.println("  EXITO: Libro registrado correctamente.");
            } else {
                System.out.println("  ERROR: Categoria \"" + categoria + "\" no valida.");
                System.out.println("  (Prueba de Arreglo: solo se aceptan categorias predefinidas)");
            }
        } else if (sub == 2) {
            System.out.print("  Nombre del usuario: ");
            String nombre = scanner.nextLine().trim();
            gestion.registrarUsuario(nombre);
            System.out.println("  EXITO: Usuario \"" + nombre + "\" registrado.");
        } else {
            System.out.println("  Subopcion invalida.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 2: BUSCAR
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuBuscar() {
        System.out.println("\n  Buscar por:");
        System.out.println("    1. ID del libro");
        System.out.println("    2. Titulo (parcial)");
        System.out.println("    3. Ver catalogo completo");
        System.out.println("    4. Ver usuarios registrados");
        int sub = leerEntero("  Opcion: ");

        if (sub == 1) {
            int id = leerEntero("  ID del libro: ");
            Libro l = gestion.buscarLibroPorId(id);
            System.out.println(l != null ? "  " + l : "  No encontrado.");
        } else if (sub == 2) {
            System.out.print("  Titulo (o fragmento): ");
            String titulo = scanner.nextLine().trim();
            List<Libro> resultados = gestion.buscarLibroPorTitulo(titulo);
            if (resultados.isEmpty()) {
                System.out.println("  Sin resultados para \"" + titulo + "\".");
            } else {
                System.out.println("  Resultados encontrados:");
                for (Libro l : resultados) System.out.println("    " + l);
            }
        } else if (sub == 3) {
            System.out.println("\n  --- CATALOGO DE LIBROS ---");
            for (Libro l : gestion.getCatalogoLibros()) System.out.println("  " + l);
        } else if (sub == 4) {
            System.out.println("\n  --- USUARIOS ---");
            for (Usuario u : gestion.getRegistroUsuarios()) System.out.println("  " + u);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 3: PRÉSTAMO
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuPrestamo() {
        System.out.println("\n  --- REALIZAR PRESTAMO ---");
        mostrarResumenRapido();
        int idLibro   = leerEntero("  ID del libro   : ");
        int idUsuario = leerEntero("  ID del usuario : ");
        System.out.println("\n  " + gestion.realizarPrestamo(idLibro, idUsuario));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 4: DEVOLUCIÓN
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuDevolucion() {
        System.out.println("\n  --- DEVOLVER LIBRO ---");
        mostrarResumenRapido();
        int idLibro   = leerEntero("  ID del libro   : ");
        int idUsuario = leerEntero("  ID del usuario : ");
        System.out.println("\n  " + gestion.devolverLibro(idLibro, idUsuario));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 5: HISTORIAL (PILA)
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuHistorial() {
        System.out.println("\n  --- HISTORIAL DE DEVOLUCIONES (PILA LIFO) ---");
        System.out.println("  El primero en aparecer es el ULTIMO devuelto:\n");
        System.out.println(gestion.mostrarHistorialDevoluciones());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 1 — OPCIÓN 6: COLA DE ESPERA
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuColaEspera() {
        System.out.println("\n  --- LISTA DE ESPERA (COLA FIFO) ---");
        int idLibro = leerEntero("  ID del libro: ");
        System.out.println();
        System.out.println(gestion.mostrarColaEspera(idLibro));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  FASE 2 — MENÚ BST (ÁRBOL)
    // ═══════════════════════════════════════════════════════════════════════

    private static void menuFase2() {
        int opcion;
        do {
            mostrarMenuArbol();
            opcion = leerEntero("Seleccione una opcion: ");
            switch (opcion) {
                case 1: arbol_insertar();       break;
                case 2: arbol_buscar();         break;
                case 3: arbol_listarInorden();  break;
                case 4: arbol_visualizar();     break;
                case 5: arbol_prestamo();       break;
                case 6: arbol_devolucion();     break;
                case 7: arbol_historial();      break;
                case 8: arbol_metricas();       break;
                case 9: break;
                default: System.out.println("  Opcion invalida.");
            }
        } while (opcion != 9);

        System.out.println("\n  Volviendo al menu principal...");
    }

    private static void mostrarMenuArbol() {
        System.out.println("\n===========================================================");
        System.out.println("         SISTEMA DE BIBLIOTECA - FASE 2: BST");
        System.out.println("===========================================================");
        System.out.println("  --- Operaciones del Arbol BST ---");
        System.out.println("  1. Insertar Libro al Arbol");
        System.out.println("  2. Buscar Libro por ID  [O(log n)]");
        System.out.println("  3. Listar Libros Ordenados  [Recorrido Inorden]");
        System.out.println("  4. Visualizar Estructura del Arbol");
        System.out.println("  --- Gestion (usando Cola y Pila integradas) ---");
        System.out.println("  5. Realizar Prestamo  [busqueda BST + Cola FIFO]");
        System.out.println("  6. Devolver Libro     [busqueda BST + Pila LIFO]");
        System.out.println("  7. Ver Historial de Devoluciones  [Pila LIFO]");
        System.out.println("  --- Analisis de Eficiencia ---");
        System.out.println("  8. Ver Metricas del Arbol  [O(n) vs O(log n)]");
        System.out.println("  9. Volver al menu principal");
        System.out.println("===========================================================");
    }

    // ───────────────────────────────────────────────────────────────────────
    //  FASE 2 — Operaciones individuales
    // ───────────────────────────────────────────────────────────────────────

    private static void arbol_insertar() {
        System.out.println("\n  --- INSERTAR LIBRO AL ARBOL BST ---");
        System.out.println("  Categorias validas:");
        String[] cats = gestionArbol.getCategorias();
        for (int i = 0; i < cats.length; i++) {
            System.out.println("    " + (i + 1) + ". " + cats[i]);
        }
        System.out.print("  Titulo    : ");
        String titulo    = scanner.nextLine().trim();
        System.out.print("  Autor     : ");
        String autor     = scanner.nextLine().trim();
        System.out.print("  Categoria : ");
        String categoria = scanner.nextLine().trim();

        boolean ok = gestionArbol.registrarLibro(titulo, autor, categoria);
        if (ok) {
            System.out.println("  EXITO: Libro insertado en el BST.");
            System.out.println("  El arbol reordena automaticamente por ID.");
        } else {
            System.out.println("  ERROR: Categoria no valida (arreglo estatico).");
        }
    }

    private static void arbol_buscar() {
        System.out.println("\n  --- BUSQUEDA BST POR ID  [O(log n)] ---");
        int id = leerEntero("  ID del libro: ");
        Libro libro = gestionArbol.buscarLibroPorId(id);
        int pasos = gestionArbol.getUltimosPasos();

        if (libro != null) {
            System.out.println("  ENCONTRADO en " + pasos + " paso(s):");
            System.out.println("  " + libro);
        } else {
            System.out.println("  No encontrado en " + pasos + " paso(s) de comparacion.");
        }
    }

    private static void arbol_listarInorden() {
        System.out.println("\n  --- RECORRIDO INORDEN (orden ascendente por ID) ---");
        System.out.println("  Esta es una propiedad gratuita del BST:");
        List<Libro> libros = gestionArbol.listarOrdenado();
        if (libros.isEmpty()) {
            System.out.println("  (Arbol vacio)");
        } else {
            for (Libro l : libros) System.out.println("  " + l);
        }
    }

    private static void arbol_visualizar() {
        System.out.println("\n  --- ESTRUCTURA VISUAL DEL ARBOL ---");
        System.out.println("  Formato: raiz -> hijos (der=mayor, izq=menor)\n");
        System.out.println(gestionArbol.visualizarArbol());
    }

    private static void arbol_prestamo() {
        System.out.println("\n  --- PRESTAMO [BST + Cola FIFO] ---");
        System.out.println("  Libros disponibles (inorden):");
        for (Libro l : gestionArbol.listarOrdenado()) System.out.println("    " + l);
        System.out.println("\n  Usuarios:");
        for (Usuario u : gestionArbol.getUsuarios()) System.out.println("    " + u);
        System.out.println();
        int idLibro   = leerEntero("  ID del libro   : ");
        int idUsuario = leerEntero("  ID del usuario : ");
        System.out.println("\n  " + gestionArbol.realizarPrestamo(idLibro, idUsuario));
    }

    private static void arbol_devolucion() {
        System.out.println("\n  --- DEVOLUCION [BST + Pila LIFO] ---");
        System.out.println("  Usuarios:");
        for (Usuario u : gestionArbol.getUsuarios()) System.out.println("    " + u);
        System.out.println();
        int idLibro   = leerEntero("  ID del libro   : ");
        int idUsuario = leerEntero("  ID del usuario : ");
        System.out.println("\n  " + gestionArbol.devolverLibro(idLibro, idUsuario));
    }

    private static void arbol_historial() {
        System.out.println("\n  --- HISTORIAL DE DEVOLUCIONES (PILA LIFO) ---");
        System.out.println("  Ultimo devuelto aparece primero:\n");
        System.out.println(gestionArbol.mostrarHistorialDevoluciones());
    }

    private static void arbol_metricas() {
        System.out.println("\n  --- ANALISIS DE EFICIENCIA ---");
        System.out.println("  Comparacion Lista O(n) vs Arbol O(log n):\n");
        System.out.println(gestionArbol.mostrarMetricas());
        System.out.println("\n  Nota: Para un BST balanceado (AVL), el peor caso");
        System.out.println("  tambien seria O(log n). El BST estandar puede");
        System.out.println("  degradarse a O(n) si los datos se insertan ordenados.");
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  UTILIDADES COMPARTIDAS
    // ═══════════════════════════════════════════════════════════════════════

    private static void mostrarResumenRapido() {
        System.out.println("  Libros:");
        for (Libro l : gestion.getCatalogoLibros()) {
            System.out.println("    " + l);
        }
        System.out.println("  Usuarios:");
        for (Usuario u : gestion.getRegistroUsuarios()) {
            System.out.println("    " + u);
        }
        System.out.println();
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Entrada invalida. Se esperaba un numero entero.");
            return -1;
        }
    }
}
