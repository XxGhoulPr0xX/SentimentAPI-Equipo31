document.addEventListener("DOMContentLoaded", () => {
    // Obtener el formulario por clase ya que no tiene id
    const langSelect = document.getElementById("langSelect");
    const commentInput = document.getElementById("commentInput")
    const statusBadge = document.getElementById("statusBadge")
    const statusText = document.getElementById("statusText")
    const submitButton = document.getElementById("submitButton")

    // Uso click en vez de submit del formulario porque Brave y otros navegadores
    // con bloqueadores de scripts hacían que se enviara como GET. Me costó encontrar eso.
    submitButton.addEventListener("click", async () => {
        const comment = commentInput.value.trim()
        if (!comment) return
        setLoadingState(true)
        try {
            const response = await fetch('/sentiment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ text: comment })
            })

            if (!response.ok) throw new Error('Error en el servidor')

            const result = await response.json()
            //Modelo de ia clasificacion verla
            //console.log("Resultado de IA:", result)

            // Mostrar resultado en el badge (usando las propiedades correctas del JSON)
            const sentimiento = result.prevision || result.etiqueta || "Neutro"
            const probabilidad = result.probabilidad || 0.5
            const porcentaje = (probabilidad * 100).toFixed(1)

            // Actualizar el texto del badge
            statusText.textContent = `${sentimiento} (${porcentaje}%)`

            // Cambiar el color del badge según el sentimiento
            statusBadge.classList.remove("positivo", "negativo", "neutro")
            if (sentimiento.toLowerCase().includes("positivo")) {
                statusBadge.classList.add("positivo")
            } else if (sentimiento.toLowerCase().includes("negativo")) {
                statusBadge.classList.add("negativo")
            } else {
                statusBadge.classList.add("neutro")
            }

            // Limpiar el input
            commentInput.value = ""

        } catch (error) {
            console.error("Error:", error)
            statusText.textContent = "Error al analizar"
            statusBadge.classList.add("negativo")
        } finally {
            setLoadingState(false)
        }
    })

    //Envia una peticion a api/config/idioma el cual contiene el lenguaje del modelo de ia
    //Este hace que cambie la direccion a donde apunta el modelo y se cambie
    langSelect.addEventListener("change", async () => {
        const selectedLang = langSelect.value; // Obtiene "es" o "en"
        
        try {
            // Enviamos la petición al controlador ApiConfig
            const response = await fetch(`/api/config/idioma?lang=${selectedLang}`, {
                method: 'POST'
            });

            if (response.ok) {
                // Feedback visual en el status badge
                statusText.textContent = `Modelo cambiado a: ${selectedLang === 'es' ? 'Español' : 'Inglés'}`;
                statusBadge.classList.add("neutro");
                console.log("Idioma cambiado exitosamente a:", selectedLang);
            } else {
                throw new Error("No se pudo cambiar el modelo en el servidor");
            }
        } catch (error) {
            console.error("Error al cambiar idioma:", error);
            statusText.textContent = "Error al cambiar idioma";
            statusBadge.classList.add("negativo");
            langSelect.value = (selectedLang === 'es' ? 'en' : 'es');
        }
    });

    function setLoadingState(isLoading) {
        if (isLoading) {
            statusBadge.classList.add("sending")
            statusBadge.classList.remove("positivo", "negativo", "neutro")
            statusText.textContent = "Analizando..."
            submitButton.disabled = true
        } else {
            statusBadge.classList.remove("sending")
            submitButton.disabled = false
        }
    }

    // Atajo de teclado: Ctrl+Enter para enviar
    commentInput.addEventListener("keydown", (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key === "Enter") {
            submitButton.click()
        }
    })
})