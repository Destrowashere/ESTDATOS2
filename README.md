# Sistema de Gestion de Biblioteca V1.0

Aplicacion de consola en Java para la gestion de bibliotecas. Implementa diversas estructuras de datos para demostrar su uso practico.

## Estructuras de Datos Utilizadas

| Estructura | Uso en el Sistema |
|------------|-------------------|
| ArrayList | Catalogo de libros y registro de usuarios |
| Stack (Pila LIFO) | Historial de devoluciones |
| Queue (Cola FIFO) | Lista de espera por libro |
| String[] | Categorias predefinidas (arreglo estatico) |
| Arbol Binario | Busqueda y organizacion de libros |

## Requisitos

- Java JDK 8 o superior instalado
- Terminal/Consola (CMD, PowerShell, o terminal de Cursor)

## Instrucciones de Ejecucion

### Paso 1: Navegar a la carpeta del proyecto

```bash
cd "c:\Users\USUARIO\Desktop\-\DEV\estrucdatos2\ESTDATOS2"
```

### Paso 2: Compilar los archivos Java

```bash
javac -d . *.java
```

### Paso 3: Ejecutar el programa

```bash
java biblioteca.Main
```

### Comando completo (PowerShell)

```powershell
cd "c:\Users\USUARIO\Desktop\-\DEV\estrucdatos2\ESTDATOS2"; javac -d . *.java; java biblioteca.Main
```

## Estructura de Archivos

```
estrucdatos2/
└── ESTDATOS2/
    ├── Main.java                    # Punto de entrada (menu principal)
    ├── GestionBiblioteca.java       # Logica principal del sistema
    ├── Libro.java                   # Clase Libro (con cola de espera)
    ├── Usuario.java                 # Clase Usuario
    ├── ArbolLibros.java             # Implementacion de arbol binario
    ├── GestionArbol.java            # Gestor del arbol de libros
    └── NodoLibro.java               # Nodo para el arbol
```

## Rutas Completas de los Archivos

| Archivo | Ruta |
|---------|------|
| Main.java | `ESTDATOS2/Main.java` |
| GestionBiblioteca.java | `ESTDATOS2/GestionBiblioteca.java` |
| Libro.java | `ESTDATOS2/Libro.java` |
| Usuario.java | `ESTDATOS2/Usuario.java` |
| ArbolLibros.java | `ESTDATOS2/ArbolLibros.java` |
| GestionArbol.java | `ESTDATOS2/GestionArbol.java` |
| NodoLibro.java | `ESTDATOS2/NodoLibro.java` |

## Funcionalidades del Sistema

1. **Registrar Libro / Usuario** - Alta de nuevos libros y usuarios
2. **Buscar Libro** - Por ID, titulo o ver catalogo completo
3. **Realizar Prestamo** - Asignar libro a usuario
4. **Devolver Libro** - Registrar devolucion y actualizar historial
5. **Ver Historial de Devoluciones** - Muestra la pila LIFO
6. **Ver Lista de Espera** - Muestra la cola FIFO de un libro

## Datos de Demo

El sistema inicia automaticamente con:
- 3 usuarios precargados
- 5 libros precargados

Esto permite probar el sistema inmediatamente sin necesidad de registrar datos manualmente.

## Notas

- Los archivos pertenecen al paquete `biblioteca`, por eso se ejecuta como `java biblioteca.Main`
- Es una aplicacion de consola con menu interactivo
- Para salir del programa, seleccionar la opcion 7 en el menu principal
