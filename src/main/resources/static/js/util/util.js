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
