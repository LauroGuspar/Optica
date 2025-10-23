const container = document.getElementById("container");
const registerBtn = document.getElementById("register");
const loginBtn = document.getElementById("login");
const registerBtnMobile = document.getElementById("registerMobile");
const loginBtnMobile = document.getElementById("loginMobile");

registerBtn.addEventListener("click", () => {
    container.classList.add("active");
});

loginBtn.addEventListener("click", () => {
    container.classList.remove("active");
});

registerBtnMobile.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.add("active");
});

loginBtnMobile.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.remove("active");
});

document.getElementById("year").textContent = new Date().getFullYear();

document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", function (event) {
        const username = document.getElementById("usuario").value;
        const password = document.getElementById("clave").value;

        if (!username || !password) {
            event.preventDefault();
            Swal.fire({
                icon: "warning",
                title: "Campos incompletos",
                text: "Por favor, ingresa tu usuario y contraseña.",
                confirmButtonColor: "#0d6efd",
            });
        }
    });

    const errorMessage = null;
    if (errorMessage) {
        Swal.fire({
            icon: "error",
            title: "Error de Acceso",
            text: errorMessage,
            confirmButtonColor: "#dc3545",
        });
    }

    const logoutMessage = null;
    if (logoutMessage) {
        Swal.fire({
            icon: "success",
            title: "Sesión Cerrada",
            text: logoutMessage,
            timer: 2000,
            showConfirmButton: false,
        });
    }
});