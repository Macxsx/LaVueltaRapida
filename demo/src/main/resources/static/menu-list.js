document.addEventListener("DOMContentLoaded", () => {
  cargarMenu();
});

async function cargarMenu() {
  const response = await fetch("database.json");
  const items = await response.json();

  generarCategorias(items);
  mostrarCategoria(items, items[0].category);
}

function generarCategorias(items) {
  const contenedor = document.getElementById("category-buttons");
  const categorias = [...new Set(items.map(i => i.category))];

  categorias.forEach(cat => {
    const btn = document.createElement("button");
    btn.textContent = cat;
    btn.addEventListener("click", () => {
      document.querySelectorAll("#category-buttons button")
        .forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      mostrarCategoria(items, cat);
    });
    contenedor.appendChild(btn);
  });

  contenedor.firstChild.classList.add("active");
}

function mostrarCategoria(items, categoria) {
  const lista = document.getElementById("menu-list");
  const titulo = document.getElementById("category-title");

  lista.innerHTML = "";
  titulo.textContent = categoria.toUpperCase();

  const filtrados = items.filter(i => i.category === categoria && i.available);

  filtrados.forEach(item => {
    const div = document.createElement("div");
    div.classList.add("menu-item");

    div.innerHTML = `
      <div class="menu-item-left">
        <img src="${item.image}" alt="${item.name}">
        <div class="menu-item-info">
          <h4>${item.name}</h4>
          <p>${item.description}</p>
        </div>
      </div>

      <div class="menu-item-right">
        <span class="menu-item-price">
          ${formatearPrecio(item.price, item.currency)}
        </span>
        <button onclick="verDetalle(${item.id})">VER</button>
      </div>
    `;

    lista.appendChild(div);
  });
}

function verDetalle(id) {
  window.location.href = `product-detail.html?id=${id}`;
}

function formatearPrecio(precio, moneda = "COP") {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: moneda,
    minimumFractionDigits: 0
  }).format(precio);
}
