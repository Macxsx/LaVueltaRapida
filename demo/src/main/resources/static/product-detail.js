// ── Subtotal dinámico ───────────────────────────────────────
const basePrice = parseFloat(document.getElementById('base-price').value) || 0;
const btnTotal  = document.getElementById('btn-total');

function formatCOP(amount) {
    return new Intl.NumberFormat('es-CO').format(amount);
}

function updateSubtotal() {
    let total = basePrice;
    document.querySelectorAll('.pd-checkbox:checked').forEach(cb => {
        total += parseFloat(cb.dataset.price) || 0;
    });
    btnTotal.textContent = '$ ' + formatCOP(total) + ' COP';
}

document.querySelectorAll('.pd-checkbox').forEach(cb => {
    cb.addEventListener('change', updateSubtotal);
});

// ── Toast al agregar ────────────────────────────────────────
const form = document.querySelector('.add-to-cart-form');
if (form) {
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        const toast = document.getElementById('toast');
        toast.classList.add('show');
        setTimeout(() => {
            toast.classList.remove('show');
            e.target.submit();
        }, 1200);
    });
}
