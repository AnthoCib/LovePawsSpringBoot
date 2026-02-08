// util.js

/**
 * Alterna la visibilidad de la contraseña en inputs tipo password
 * Compatible con el login y formularios similares
 */
function togglePassword(btn) {
    const input = document.getElementById('password');
    const icon = btn.querySelector('i');
    const label = document.getElementById('eyeLabel1');

    if (!input || !icon) return;

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('bi-eye', 'bi-eye-slash');
        if (label) label.textContent = 'Ocultar contraseña';
    } else {
        input.type = 'password';
        icon.classList.replace('bi-eye-slash', 'bi-eye');
        if (label) label.textContent = 'Mostrar contraseña';
    }
}

/**
 * Alterna la visibilidad de un input de contraseña específico.
 * @param {string} inputId - ID del input a alternar.
 * @param {HTMLButtonElement} btn - Botón que dispara la acción.
 */
function togglePasswordField(inputId, btn) {
    const input = document.getElementById(inputId);
    const icon = btn ? btn.querySelector('i') : null;
    const labelId = btn ? btn.getAttribute('data-label-id') : null;
    const label = labelId ? document.getElementById(labelId) : null;

    if (!input || !icon) return;

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('bi-eye', 'bi-eye-slash');
        if (label) label.textContent = 'Ocultar contraseña';
    } else {
        input.type = 'password';
        icon.classList.replace('bi-eye-slash', 'bi-eye');
        if (label) label.textContent = 'Mostrar contraseña';
    }
}

/**
 * Confirma la eliminación de una mascota antes de redirigir
 * @param {number|string} id - ID de la mascota
 */
function verificar(id) {
    Swal.fire({
        title: "¿Estás seguro?",
        text: "¡Esta acción eliminará la mascota!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "EliminarMascota?id=" + id;
        }
    });
}

/**
 * Cambia la especie seleccionada y envía el formulario automáticamente
 */
function setEspecie(value) {
    const inputHidden = document.getElementById("especieHidden");
    if (!inputHidden) return;
    inputHidden.value = value;
    document.forms[0].submit();
}

/**
 * Muestra un mensaje SweetAlert general (puedes reutilizarlo)
 * @param {string} tipo - success | error | warning | info
 * @param {string} titulo - título del mensaje
 * @param {string} texto - contenido del mensaje
 */
function mostrarAlerta(tipo, titulo, texto) {
    Swal.fire({
        icon: tipo,
        title: titulo,
        text: texto,
        confirmButtonColor: "#3085d6"
    });
}

/**
 * Agrega confirmación SweetAlert a formularios con data-confirm.
 */
function initConfirmableForms() {
    const forms = document.querySelectorAll("form[data-confirm='true']");
    if (!forms.length) return;

    forms.forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (form.dataset.confirmed === "true") {
                return;
            }

            event.preventDefault();

            const title = form.dataset.confirmTitle || "¿Estás seguro?";
            const text = form.dataset.confirmText || "Esta acción no se puede deshacer.";
            const confirmText = form.dataset.confirmButton || "Confirmar";
            const cancelText = form.dataset.cancelButton || "Cancelar";
            const icon = form.dataset.confirmIcon || "warning";

            Swal.fire({
                title,
                text,
                icon,
                showCancelButton: true,
                confirmButtonText: confirmText,
                cancelButtonText: cancelText,
                confirmButtonColor: "#d33",
                cancelButtonColor: "#3085d6"
            }).then((result) => {
                if (result.isConfirmed) {
                    form.dataset.confirmed = "true";
                    form.submit();
                }
            });
        });
    });
}

document.addEventListener("DOMContentLoaded", initConfirmableForms);
