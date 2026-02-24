document.addEventListener("DOMContentLoaded", () => {
  cargarDetalle();
});

async function cargarDetalle() {
  try {
    const params = new URLSearchParams(window.location.search);
    const id = parseInt(params.get("id"));

    if (!id) {
      console.error("No se recibió ID en la URL");
      return;
    }

    const response = await fetch("database.json");
    const items = await response.json();

    if (!Array.isArray(items)) {
      console.error("database.json no es válido");
      return;
    }

    const producto = items.find(item => item.id === id);

    if (!producto) {
      console.error("Producto no encontrado");
      return;
    }

    mostrarProducto(producto);
    generarRecomendaciones(items, producto);

  } catch (error) {
    console.error("Error cargando detalle:", error);
  }
}

function mostrarProducto(producto) {
  document.getElementById("product-image").src = producto.image;
  document.getElementById("product-image").alt = producto.name;

  document.getElementById("product-name").textContent = producto.name;
  document.getElementById("product-description").textContent = producto.description;

  document.getElementById("product-price").textContent =
    formatearPrecio(producto.price, producto.currency);
}

function generarRecomendaciones(items, productoActual) {

  const contenedor = document.getElementById("recommendations-grid");
  contenedor.innerHTML = "";

  const similares = items.filter(item =>
    item.category === productoActual.category &&
    item.id !== productoActual.id &&
    item.available
  );

  const seleccion = similares.slice(0, 2); // solo 2 recomendaciones

  seleccion.forEach(item => {

    const card = document.createElement("div");
    card.classList.add("recommendation-card");

    card.innerHTML = `
      <img src="${item.image}" alt="${item.name}">
      <h4>${item.name}</h4>
      <p>${formatearPrecio(item.price, item.currency)}</p>
    `;

    card.addEventListener("click", () => {
      window.location.href = `product-detail.html?id=${item.id}`;
    });

    contenedor.appendChild(card);
  });
}

function formatearPrecio(precio, moneda = "COP") {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: moneda,
    minimumFractionDigits: 0
  }).format(precio);
}
