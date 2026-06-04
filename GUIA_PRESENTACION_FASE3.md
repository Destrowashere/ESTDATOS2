# Guía de presentación — Fase 3: Grafos

Duración sugerida: 8-10 minutos.

---

## Diapositiva 1 — Título

- Implementación de grafos en sistema bibliotecario
- Fase 3: Estructuras de Datos II
- [Integrantes]

---

## Diapositiva 2 — Contexto

- Fase 1: listas, pilas, colas — O(n)
- Fase 2: BST — O(log n) búsqueda por ID
- Fase 3: relaciones usuario-libro — grafo

---

## Diapositiva 3 — Problema

- Sin grafo: recomendar = comparar todos con todos
- Complejidad O(usuarios × libros)
- Necesidad: modelo explícito de interacciones

---

## Diapositiva 4 — Tipo de grafo elegido

- **Bipartito:** solo U y L
- **Dirigido:** usuario → libro
- **Ponderado:** historial acumula peso
- **Lista de adyacencia:** grafo disperso

---

## Diapositiva 5 — Diseño

- Nodos: `U:1`, `L:2`
- Aristas: PRESTAMO_ACTIVO, ESPERA, HISTORIAL
- Diagrama en pizarra o captura opción 6 del menú

---

## Diapositiva 6 — Demo en vivo

**Opción A (impacto visual):** abrir `visualizador-grafo/index.html` en el navegador — grafo interactivo.

**Opción B (consola):** `java biblioteca.Main` → 8 → opción 6 y 5.

---

## Diapositiva 7 — Pruebas y optimización

- `java biblioteca.PruebasGrafo` — 5 casos OK
- Menú opción 8 — escenario guiado
- Métricas opción 7: grafo vs lista

---

## Diapositiva 8 — Conclusiones

- CRUD nodos/aristas implementado
- BFS para recomendaciones
- Integración con pila y cola
- Limitación: fases con datos separados

---

## Preguntas frecuentes (preparación)

**¿Por qué bipartito?** Solo hay dos tipos de entidad; no modelamos red social entre usuarios.

**¿Por qué dirigido?** El libro no inicia el préstamo hacia el usuario.

**¿Qué es el peso?** Número de veces o intensidad del vínculo (historial).

**Complejidad BFS?** O(V + E) en el peor caso del recorrido.

---

## Comandos para la demo

```powershell
cd "c:\Users\USUARIO\Desktop\-\DEV\estdatos2clone\ESTDATOS2"
javac -d . *.java
java biblioteca.Main
```

```powershell
java biblioteca.PruebasGrafo
```
