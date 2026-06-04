/* global vis, GRAFO_DEMO, construirDataset, generarAdyacenciaTexto, recomendarLibros, COLORES_ARISTA */

let datosActuales = JSON.parse(JSON.stringify(GRAFO_DEMO));
let network = null;
let nodesDS = null;
let edgesDS = null;

const options = {
  nodes: {
    shape: "dot",
    size: 22,
    font: { color: "#e8edf4", size: 12, face: "DM Sans" },
    borderWidth: 2,
    shadow: true,
  },
  groups: {
    usuario: {
      color: { background: "#1e3a5f", border: "#60a5fa", highlight: { background: "#2563eb", border: "#93c5fd" } },
      size: 26,
    },
    libro: {
      color: { background: "#422006", border: "#f59e0b", highlight: { background: "#b45309", border: "#fcd34d" } },
      size: 20,
    },
  },
  edges: {
    width: 2,
    smooth: { type: "cubicBezier", forceDirection: "horizontal", roundness: 0.4 },
  },
  physics: {
    enabled: true,
    solver: "forceAtlas2Based",
    forceAtlas2Based: {
      gravitationalConstant: -45,
      centralGravity: 0.008,
      springLength: 180,
      springConstant: 0.12,
    },
    stabilization: { iterations: 120 },
  },
  interaction: {
    hover: true,
    tooltipDelay: 80,
    navigationButtons: false,
  },
  layout: { improvedLayout: true },
};

function init() {
  const container = document.getElementById("grafoNetwork");
  const built = construirDataset(datosActuales);
  nodesDS = built.nodes;
  edgesDS = built.edges;
  network = new vis.Network(container, { nodes: nodesDS, edges: edgesDS }, options);

  network.on("click", onGraphClick);
  network.once("stabilizationIterationsDone", () => network.fit({ animation: true }));

  poblarSelectUsuarios();
  actualizarStats();
  document.getElementById("adyacenciaText").textContent = generarAdyacenciaTexto(datosActuales);

  document.getElementById("btnRecomendar").addEventListener("click", ejecutarRecomendaciones);
  document.getElementById("btnReset").addEventListener("click", restaurarDemo);
  document.getElementById("btnFit").addEventListener("click", () => network.fit({ animation: true }));
}

function poblarSelectUsuarios() {
  const sel = document.getElementById("selectUsuario");
  sel.innerHTML = "";
  datosActuales.usuarios.forEach((u) => {
    const opt = document.createElement("option");
    opt.value = u.id;
    opt.textContent = u.nombre + " (U:" + u.id + ")";
    sel.appendChild(opt);
  });
}

function actualizarStats() {
  const nUsuarios = datosActuales.usuarios.length;
  const nLibros = datosActuales.libros.length;
  const nAristas = datosActuales.aristas.length;
  document.getElementById("statNodos").textContent = nUsuarios + nLibros;
  document.getElementById("statAristas").textContent = nAristas;
  document.getElementById("statUsuarios").textContent = nUsuarios;
  document.getElementById("statLibros").textContent = nLibros;
}

function onGraphClick(params) {
  const detalle = document.getElementById("detalleNodo");
  if (params.edges.length > 0) {
    const edgeId = params.edges[0];
    const edge = edgesDS.get(edgeId);
    const estilo = COLORES_ARISTA[edge.tipo] || {};
    detalle.innerHTML = `
      <h3>Arista dirigida</h3>
      <p><span class="tag tag--user">${edge.from}</span> → <span class="tag tag--book">${edge.to}</span></p>
      <p><strong>Tipo:</strong> ${edge.tipo}</p>
      <p><strong>Peso:</strong> ${edge.peso}</p>
      <p style="color:var(--muted);margin-top:0.5rem">Operación: buscarArista() / eliminarArista() en GrafoBiblioteca.java</p>
    `;
    return;
  }
  if (params.nodes.length > 0) {
    const nodeId = params.nodes[0];
    const node = nodesDS.get(nodeId);
    const esUsuario = node.group === "usuario";
    const u = datosActuales.usuarios.find((x) => "U:" + x.id === nodeId);
    const l = datosActuales.libros.find((x) => "L:" + x.id === nodeId);
    const salientes = datosActuales.aristas.filter((a) => a.from === nodeId);
    let vecinosHtml = salientes.length
      ? salientes.map((a) => `<li>${a.to} [${a.tipo}, w=${a.peso}]</li>`).join("")
      : "<li>(sin aristas salientes)</li>";
    detalle.innerHTML = `
      <h3>${esUsuario ? u.nombre : l.titulo}</h3>
      <p><span class="tag ${esUsuario ? "tag--user" : "tag--book"}">${nodeId}</span>
         <span class="tag">${esUsuario ? "USUARIO" : "LIBRO"}</span></p>
      ${esUsuario ? "" : `<p>${l.autor} · ${l.categoria}</p>`}
      <p><strong>Vecinos (adyacencia):</strong></p>
      <ul class="rec-list">${vecinosHtml}</ul>
      <p style="color:var(--muted);margin-top:0.5rem">Operación: obtenerVecinos() — O(grado)</p>
    `;
  }
}

function ejecutarRecomendaciones() {
  const id = parseInt(document.getElementById("selectUsuario").value, 10);
  const recs = recomendarLibros(id, datosActuales, 5);
  const lista = document.getElementById("listaRecomendaciones");
  const u = datosActuales.usuarios.find((x) => x.id === id);
  if (recs.length === 0) {
    lista.innerHTML = "<li>Sin recomendaciones para este usuario.</li>";
    return;
  }
  lista.innerHTML = recs
    .map(
      (r, i) =>
        `<li>${i + 1}. <strong>${r.titulo}</strong> (L:${r.id}) — afinidad ${r.puntuacion}</li>`
    )
    .join("");
  document.getElementById("detalleNodo").innerHTML = `
    <h3>BFS / filtrado colaborativo</h3>
    <p>Recomendaciones para <strong>${u.nombre}</strong>:</p>
    <ul class="rec-list">${lista.innerHTML}</ul>
    <p style="color:var(--muted);margin-top:0.5rem">Método: recomendarLibros() en GrafoBiblioteca.java</p>
  `;
}

function restaurarDemo() {
  datosActuales = JSON.parse(JSON.stringify(GRAFO_DEMO));
  const built = construirDataset(datosActuales);
  nodesDS.clear();
  edgesDS.clear();
  nodesDS.add(built.nodes.get());
  edgesDS.add(built.edges.get());
  network.fit({ animation: true });
  actualizarStats();
  document.getElementById("adyacenciaText").textContent = generarAdyacenciaTexto(datosActuales);
  document.getElementById("listaRecomendaciones").innerHTML = "";
  document.getElementById("detalleNodo").innerHTML =
    '<p class="detail-placeholder">Demo restaurada. Haz clic en el grafo.</p>';
}

document.addEventListener("DOMContentLoaded", init);
