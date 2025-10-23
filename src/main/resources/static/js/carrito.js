
const STORAGE_KEY = "optica_cart";

const defaultCart = [];

let carrito = [];

function loadCart() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY);
        carrito = raw
            ? JSON.parse(raw)
            : JSON.parse(JSON.stringify(defaultCart));
        saveCart();
    } catch (e) {
        console.error(
            "No se pudo leer el carrito desde localStorage, usando valores por defecto.",
            e
        );
        carrito = JSON.parse(JSON.stringify(defaultCart));
    }
}

function saveCart() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(carrito));
    updateCartBadge();
}

function updateCartBadge() {
    const count = carrito.reduce((s, p) => s + p.cantidad, 0);
    const el = document.getElementById("cartCount");
    if (el) el.textContent = count;
}

function renderCarrito() {
    const tbody = document.getElementById("tbodyCarrito");
    tbody.innerHTML = "";
    let total = 0;

    carrito.forEach((p, i) => {
        const subtotal = p.precio * p.cantidad;
        total += subtotal;
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${p.nombre}</td>
          <td><input type="number" min="1" value="${p.cantidad}" class="form-control form-control-sm" data-index="${i}"></td>
          <td>S/ ${p.precio}</td>
          <td>S/ ${subtotal}</td>
          <td><button class="btn btn-sm btn-outline-danger" data-eliminar="${i}" title="Eliminar"><i class="bi bi-trash"></i></button></td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById("total").textContent = `S/ ${total.toFixed(2)}`;
    saveCart();
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("year").textContent = new Date().getFullYear();

    loadCart();
    renderCarrito();

    document
        .getElementById("tbodyCarrito")
        .addEventListener("input", (e) => {
            if (e.target.matches('input[type="number"]')) {
                const i = Number(e.target.dataset.index);
                carrito[i].cantidad = Math.max(1, parseInt(e.target.value) || 1);
                renderCarrito();
            }
        });

    document
        .getElementById("tbodyCarrito")
        .addEventListener("click", (e) => {
            const btn = e.target.closest("button[data-eliminar]");
            if (!btn) return;
            const i = Number(btn.dataset.eliminar);
            if (isNaN(i)) return;
            if (!confirm("¿Eliminar este producto del carrito?")) return;
            const toastElement = document.getElementById("liveToastSuccess");
            toastElement.querySelector(".toast-body").textContent = `Se ha eliminado el producto del carrito.`;
            const toast = new bootstrap.Toast(toastElement);
            toast.show();
            carrito.splice(i, 1);
            renderCarrito();
        });

    document.getElementById("btnVaciar").addEventListener("click", () => {
        Swal.fire({
            title: "¿Estás seguro?",
            text: "No podrás revertir esto.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Sí, ¡bórralo!",
        }).then((result) => {
            if (result.isConfirmed) {
                carrito = [];
                renderCarrito();
                actualizarBadge();
                const toastElement = document.getElementById("liveToastSuccess");
                toastElement.querySelector(".toast-body").textContent = `Se ha vaciado el carrito.`;
                const toast = new bootstrap.Toast(toastElement);
                toast.show();
            }
        });

    });

    document.getElementById("pagoYape").addEventListener("change", () => {
        document.getElementById("qrYape").classList.remove("d-none");
        document.getElementById("formTarjeta").classList.add("d-none");
    });

    document
        .getElementById("pagoTarjeta")
        .addEventListener("change", () => {
            document.getElementById("qrYape").classList.add("d-none");
            document.getElementById("formTarjeta").classList.remove("d-none");
        });

    document
        .getElementById("btnFinalizar")
        .addEventListener("click", () => {
            const pagoTarjeta = document.getElementById("pagoTarjeta").checked;
            if (carrito.length === 0) {
                alert("El carrito está vacío.");
                return;
            }
            if (pagoTarjeta) {
                const num = document
                    .getElementById("cardNumber")
                    .value.replace(/\s+/g, "");
                const exp = document.getElementById("cardExpiry").value;
                const cvv = document.getElementById("cardCVV").value;
                const name = document.getElementById("cardName").value.trim();
                if (!/^\d{13,19}$/.test(num)) {
                    alert(
                        "Ingrese un número de tarjeta válido (solo dígitos, 13-19 caracteres)."
                    );
                    return;
                }
                if (!/^(0[1-9]|1[0-2])\/[0-9]{2}$/.test(exp)) {
                    alert("Formato de vencimiento inválido. Use MM/AA.");
                    return;
                }
                if (!/^\d{3,4}$/.test(cvv)) {
                    alert("CVV inválido.");
                    return;
                }
                if (name.length < 3) {
                    alert("Ingrese el nombre del titular.");
                    return;
                }
                alert("Pago simulado aceptado. (Demo)\nGracias por tu compra.");
            } else {
                alert(
                    "Por favor, escanea el QR de Yape y completa el pago. (Demo)"
                );
            }
        });
});


window._optica = {
    resetDemo: () => {
        localStorage.removeItem(STORAGE_KEY);
        loadCart();
        renderCarrito();
    },
    getCart: () => JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]"),
};