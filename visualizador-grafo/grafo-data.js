/**
 * Datos demo — sincronizados con GestionGrafo.cargarDatosDemo() en Java.
 * Misma topología para capturas del informe (Implementación visual).
 */
const GRAFO_DEMO = {
  usuarios: [
    { id: 1, nombre: "Ana Garcia" },
    { id: 2, nombre: "Carlos Lopez" },
    { id: 3, nombre: "Maria Perez" },
  ],
  libros: [
    { id: 1, titulo: "El Resplandor", autor: "Stephen King", categoria: "Terror" },
    { id: 2, titulo: "Cosmos", autor: "Carl Sagan", categoria: "Ciencia" },
    { id: 3, titulo: "Sapiens", autor: "Yuval Noah Harari", categoria: "Historia" },
    { id: 4, titulo: "El Senor de los Anillos", autor: "J.R.R. Tolkien", categoria: "Fantasia" },
    { id: 5, titulo: "Breve Historia del Tiempo", autor: "Stephen Hawking", categoria: "Ciencia" },
    { id: 6, titulo: "1984", autor: "George Orwell", categoria: "Historia" },
    { id: 7, titulo: "Dune", autor: "Frank Herbert", categoria: "Fantasia" },
  ],
  aristas: [
    { from: "U:1", to: "L:2", tipo: "PRESTAMO_ACTIVO", peso: 1 },
    { from: "U:2", to: "L:3", tipo: "HISTORIAL", peso: 2 },
    { from: "U:2", to: "L:2", tipo: "HISTORIAL", peso: 1 },
    { from: "U:2", to: "L:7", tipo: "PRESTAMO_ACTIVO", peso: 1 },
    { from: "U:3", to: "L:7", tipo: "ESPERA", peso: 1 },
  ],
};

const COLORES_ARISTA = {
  PRESTAMO_ACTIVO: { color: "#22c55e", highlight: "#4ade80" },
  ESPERA: { color: "#f97316", highlight: "#fb923c" },
  HISTORIAL: { color: "#94a3b8", highlight: "#cbd5e1" },
};

function claveUsuario(id) {
  return "U:" + id;
}

function claveLibro(id) {
  return "L:" + id;
}

function construirDataset(datos) {
  const nodes = [];
  const edges = [];

  datos.usuarios.forEach((u, i) => {
    nodes.push({
      id: claveUsuario(u.id),
      label: u.nombre.split(" ")[0] + "\n" + u.nombre.split(" ").slice(1).join(" "),
      title: `<b>${u.nombre}</b><br>ID: ${u.id}<br>Tipo: USUARIO`,
      group: "usuario",
      x: -320,
      y: -120 + i * 120,
      fixed: { x: false, y: false },
    });
  });

  datos.libros.forEach((l, i) => {
    nodes.push({
      id: claveLibro(l.id),
      label: truncar(l.titulo, 18),
      title: `<b>${l.titulo}</b><br>${l.autor}<br>${l.categoria}<br>ID: ${l.id}`,
      group: "libro",
      x: 320,
      y: -180 + i * 52,
      fixed: { x: false, y: false },
    });
  });

  datos.aristas.forEach((a, idx) => {
    const estilo = COLORES_ARISTA[a.tipo] || COLORES_ARISTA.HISTORIAL;
    edges.push({
      id: "e" + idx,
      from: a.from,
      to: a.to,
      label: a.tipo.replace("_", " ") + " w=" + a.peso,
      title: `${a.from} → ${a.to}<br>${a.tipo}<br>Peso: ${a.peso}`,
      arrows: "to",
      color: estilo,
      font: { align: "middle", size: 10, color: "#8b9cb3" },
      tipo: a.tipo,
      peso: a.peso,
    });
  });

  return { nodes: new vis.DataSet(nodes), edges: new vis.DataSet(edges) };
}

function truncar(s, max) {
  return s.length <= max ? s : s.slice(0, max - 1) + "…";
}

function generarAdyacenciaTexto(datos) {
  const mapa = {};
  datos.usuarios.forEach((u) => {
    mapa[claveUsuario(u.id)] = [];
  });
  datos.libros.forEach((l) => {
    mapa[claveLibro(l.id)] = [];
  });
  datos.aristas.forEach((a) => {
    if (!mapa[a.from]) mapa[a.from] = [];
    mapa[a.from].push(`→ ${a.to} [${a.tipo}, w=${a.peso}]`);
  });

  let texto = "--- LISTA DE ADYACENCIA ---\n";
  Object.keys(mapa)
    .sort()
    .forEach((k) => {
      texto += k + "\n";
      const lista = mapa[k];
      if (lista.length === 0) texto += "  (sin aristas salientes)\n";
      else lista.forEach((line) => (texto += "  " + line + "\n"));
    });
  return texto.trim();
}

/**
 * Recomendaciones colaborativas U → L → U → L (misma idea que GrafoBiblioteca.recomendarLibros).
 */
function recomendarLibros(idUsuario, datos, max) {
  const inicio = claveUsuario(idUsuario);
  const puntuacion = {};

  datos.aristas
    .filter((a) => a.from === inicio)
    .forEach((a1) => {
      const claveL1 = a1.to;
      datos.usuarios.forEach((u) => {
        const claveU2 = claveUsuario(u.id);
        if (claveU2 === inicio) return;
        const a2 = datos.aristas.find((x) => x.from === claveU2 && x.to === claveL1);
        if (!a2) return;
        datos.aristas
          .filter((a3) => a3.from === claveU2 && a3.to.startsWith("L:") && a3.to !== claveL1)
          .forEach((a3) => {
            const pts = a1.peso + a2.peso + a3.peso;
            puntuacion[a3.to] = (puntuacion[a3.to] || 0) + pts;
          });
      });
    });

  return Object.entries(puntuacion)
    .sort((a, b) => b[1] - a[1])
    .slice(0, max)
    .map(([clave, pts]) => {
      const id = parseInt(clave.slice(2), 10);
      const libro = datos.libros.find((l) => l.id === id);
      return { id, titulo: libro ? libro.titulo : clave, puntuacion: pts };
    });
}
