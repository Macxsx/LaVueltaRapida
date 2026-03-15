document.querySelector(".add-to-cart-form").addEventListener("submit", function(e) {
    e.preventDefault();

    const toast = document.getElementById("toast");
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
        e.target.submit();
    }, 1200);
});

