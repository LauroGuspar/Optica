const $ = (sel, ctx = document) => ctx.querySelector(sel);

function getCart() {
    return JSON.parse(localStorage.getItem("optica_cart")) || [];
}

function actualizarBadge() {
    const cart = getCart();
    const total = cart.reduce((acc, p) => acc + p.cantidad, 0);
    const badge = document.querySelector(".nav-link[title='Carrito'] .badge");
    if (badge) badge.textContent = total;
}

document.addEventListener("DOMContentLoaded", () => {
    $("#year").textContent = new Date().getFullYear();
    actualizarBadge();
});