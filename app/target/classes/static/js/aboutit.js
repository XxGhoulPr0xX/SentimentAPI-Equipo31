// 1. Función para Agregar una Reseña
function agrega_reseña() {
    console.log("Botón 'Agregar' presionado");
    
    // Aquí podrías abrir un modal o redirigir a un formulario
    const nuevaReseña = prompt("Introduce el título del producto:");
    
    if (nuevaReseña) {
        alert("Función lista: Aquí enviaremos '" + nuevaReseña + "' al Backend para guardarlo en el CSV.");
        // Lógica futura: fetch('/api/agregar', { method: 'POST', body: ... })
    }
}

// 2. Función para Editar un Registro
// Nota: Cuando tengas los datos reales, esta función recibirá un ID
function edita_registro(id) {
    console.log("Editando el registro ID: " + id);
    
    alert("Función lista: Se abrirá un formulario con los datos actuales del ID " + id + " para modificarlos.");
    // Lógica futura: fetch('/api/editar/' + id, { method: 'PUT', body: ... })
}

// 3. Función para Eliminar un Registro
function elimina_registro(id) {
    console.log("Intentando eliminar el registro ID: " + id);
    
    const confirmar = confirm("¿Estás seguro de que deseas eliminar esta reseña?");
    
    if (confirmar) {
        alert("Función lista: Se enviará la orden al Backend para borrar el registro " + id + " del CSV.");
        // Lógica futura: fetch('/api/eliminar/' + id, { method: 'DELETE' })
    }
}

// 4. Lógica del Buscador (Opcional pero útil)
const inputBuscador = document.getElementById('buscador');

if(inputBuscador) {
    inputBuscador.addEventListener('input', (e) => {
        const texto = e.target.value.toLowerCase();
        console.log("Buscando: " + texto);
        // Aquí filtrarás las "cards" que creamos en el paso anterior
    });
}