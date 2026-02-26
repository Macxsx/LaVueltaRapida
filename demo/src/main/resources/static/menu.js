document.addEventListener("DOMContentLoaded", () => {
  cargarMenu();
});

async function cargarMenu() {
  try {
    const response = await fetch("database.json");
    const items = await response.json(); // <-- ahora es array directo

    if (!Array.isArray(items)) {
      console.error("database.json no es un array válido");
      return;
    }

    renderizarCategorias(items);

  } catch (error) {
    console.error("Error cargando el menú:", error);
  }
}

function renderizarCategorias(items) {

  const categorias = {
    clasicas: document.getElementById("clasicas-grid"),
    especiales: document.getElementById("especiales-grid"),
    picantes: document.getElementById("picantes-grid"),
    bebidas: document.getElementById("bebidas-grid"),
    postres: document.getElementById("postres-grid")
  };

  // Limpiar grids
  Object.values(categorias).forEach(grid => {
    if (grid) grid.innerHTML = "";
  });

  items.forEach(item => {

    // Solo mostrar si está disponible
    if (!item.available) return;

    const categoria = item.category?.toLowerCase();

    if (!categorias[categoria]) return;

    const card = crearCard(item);
    categorias[categoria].appendChild(card);
  });
}

function crearCard(item) {

  const card = document.createElement("div");
  card.classList.add("menu-card");

  card.innerHTML = `
    <img src="${item.image}" alt="${item.name}">
    <h3>${item.name}</h3>
    <p>${item.description}</p>
    <div class="price">${formatearPrecio(item.price, item.currency)}</div>
  `;

  card.addEventListener("click", () => {
    window.location.href = `product-detail.html?id=${item.id}`;
  });

  return card;
}

function formatearPrecio(precio, moneda = "COP") {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: moneda,
    minimumFractionDigits: 0
  }).format(precio);
}
