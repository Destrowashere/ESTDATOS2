# Sistema de Gestion de Biblioteca V1.0

Aplicacion de consola en Java para la gestion de bibliotecas. Implementa diversas estructuras de datos para demostrar su uso practico en tres fases.

## Estructuras de Datos Utilizadas

| Estructura | Fase | Uso en el Sistema |
|------------|------|-------------------|
| ArrayList | 1, 3 | Catalogo de libros y registro de usuarios |
| Stack (Pila LIFO) | 1, 2, 3 | Historial de devoluciones |
| Queue (Cola FIFO) | 1, 2, 3 | Lista de espera por libro |
| String[] | 1, 2, 3 | Categorias predefinidas (arreglo estatico) |
| Arbol Binario (BST) | 2 | Busqueda y organizacion de libros por ID |
| Grafo bipartito dirigido ponderado | 3 | Relaciones usuario-libro y recomendaciones (BFS) |

## Requisitos

- Java JDK 8 o superior instalado
- Terminal/Consola (CMD, PowerShell, o terminal de Cursor)

## Instrucciones de Ejecucion

### Paso 1: Navegar a la carpeta del proyecto

```powershell
cd "c:\Users\USUARIO\Desktop\-\DEV\estdatos2clone\ESTDATOS2"
```

### Paso 2: Compilar los archivos Java

```powershell
javac -d . *.java
```

### Paso 3: Ejecutar el programa

```powershell
java biblioteca.Main
```

### Pruebas automaticas Fase 3 (grafos)

```powershell
java biblioteca.PruebasGrafo
```

### Comando completo (PowerShell)

```powershell
cd "c:\Users\USUARIO\Desktop\-\DEV\estdatos2clone\ESTDATOS2"; javac -d . *.java; java biblioteca.Main
```

## Estructura de Archivos

```
ESTDATOS2/
├── Main.java                    # Punto de entrada (menu principal)
├── GestionBiblioteca.java       # Logica Fase 1
├── GestionArbol.java            # Logica Fase 2
├── GestionGrafo.java            # Logica Fase 3
├── GrafoBiblioteca.java         # Grafo bipartito (lista adyacencia)
├── TipoNodo.java                # Enum USUARIO / LIBRO
├── TipoRelacion.java            # Enum tipos de arista
├── Arista.java                  # Arista ponderada
├── PruebasGrafo.java            # Pruebas automaticas Fase 3
├── Libro.java                   # Clase Libro (con cola de espera)
├── Usuario.java                 # Clase Usuario
├── ArbolLibros.java             # BST del catalogo
├── GestionArbol.java            # (ver arriba)
├── NodoLibro.java               # Nodo del arbol
├── GUIA_PRESENTACION_FASE3.md    # Guion de presentacion
├── visualizador-grafo/           # Prototipo web HTML/CSS/JS del grafo
│   ├── index.html
│   ├── styles.css
│   ├── grafo-data.js
│   └── app.js
├── INFORME_APA_BORRADOR.md      # Borrador informe (gitignored, local)
└── README.md
```

## Funcionalidades del Sistema

### Fase 1 — Estructuras lineales (menu principal 1-6)

1. Registrar Libro / Usuario
2. Buscar Libro (por Titulo o ID)
3. Realizar Prestamo
4. Devolver Libro
5. Ver Historial de Devoluciones (Pila LIFO)
6. Ver Lista de Espera de un Libro (Cola FIFO)

### Fase 2 — Arbol BST (menu principal opcion 7)

- Insertar, buscar O(log n), listar inorden, visualizar arbol
- Prestamo, devolucion, historial, metricas

### Fase 3 — Grafos (menu principal opcion 8)

1. Registrar Libro / Usuario (+ nodo en grafo)
2. Realizar Prestamo (arista PRESTAMO_ACTIVO o ESPERA)
3. Devolver Libro (arista HISTORIAL + pila/cola)
4. Ver Vecinos de Usuario o Libro
5. Recomendaciones BFS para un Usuario
6. Visualizar Grafo (lista de adyacencia)
7. Metricas de eficiencia (grafo vs lista)
8. Escenario de prueba guiado
9. Volver al menu principal

**Salir del programa:** opcion 9 en el menu principal.

## Datos de Demo (Fase 3)

Al iniciar Fase 3 se cargan automaticamente:

- 7 libros y 3 usuarios (Ana, Carlos, Maria)
- Ana tiene prestado Cosmos (L:2)
- Carlos tiene historial con Sapiens y Cosmos; tiene Dune prestado
- Maria en espera por Dune

Ideal para probar vecinos, recomendaciones y visualizacion del grafo sin registrar datos manualmente.

## Visualizador web del grafo (Implementacion — HTML/CSS/JS)

Prototipo visual para el informe APA (seccion Implementacion y capturas). Mismos datos demo que `GestionGrafo.java`.

**Abrir en el navegador:**

1. Ir a la carpeta `ESTDATOS2/visualizador-grafo/`
2. Doble clic en `index.html` o usar Live Server en VS Code/Cursor

**Incluye:**

- Grafo bipartito interactivo (vis-network): usuarios a la izquierda, libros a la derecha
- Aristas coloreadas: PRESTAMO_ACTIVO, ESPERA, HISTORIAL
- Clic en nodos/aristas → detalle y lista de adyacencia
- Boton de recomendaciones BFS (misma logica que Java)
- Panel de estadisticas (nodos, aristas)

**Archivos:** `index.html`, `styles.css`, `grafo-data.js`, `app.js`

**Stack:** HTML5, CSS3, JavaScript, [vis-network](https://visjs.github.io/vis-network/), Google Fonts (DM Sans, JetBrains Mono), unpkg CDN. Detalle y citas APA en `visualizador-grafo/STACK.md`.

No requiere compilar Java. Complementa la consola (`java biblioteca.Main` opcion 8).

## Informe academico (APA 7)

El borrador del informe con conceptos, justificaciones y guia de capturas esta en `INFORME_APA_BORRADOR.md` (archivo local, excluido de git). Usalo como base para tu PDF.

## Notas

- Los archivos pertenecen al paquete `biblioteca`
- Las tres fases usan instancias independientes (catalogos separados)
- Es una aplicacion de consola con menu interactivo
