const form = document.querySelector(".add-to-cart-form");
const btn  = form.querySelector(".pd-btn-order");
const btnText     = btn.querySelector(".pd-btn-text");
const btnSubtotal = btn.querySelector(".pd-btn-subtotal");
const checkboxes  = form.querySelectorAll(".pd-checkbox");

const basePrice = parseFloat(form.dataset.basePrice) || 0;

function formatCOP(n) {
  return "$ " + Math.round(n).toLocaleString("es-CO").replace(/,/g, ".") + " COP";
}

function updateSubtotal() {
  let extras = 0;
  checkboxes.forEach(cb => {
    if (cb.checked) extras += parseFloat(cb.dataset.price) || 0;
  });
  const total = basePrice + extras;
  btnSubtotal.textContent = "— " + formatCOP(total);
}

checkboxes.forEach(cb => cb.addEventListener("change", updateSubtotal));
updateSubtotal();

form.addEventListener("submit", function(e) {
  e.preventDefault();
  const toast = document.getElementById("toast");
  toast.classList.add("show");
  setTimeout(() => {
    toast.classList.remove("show");
    e.target.submit();
  }, 1200);
});
