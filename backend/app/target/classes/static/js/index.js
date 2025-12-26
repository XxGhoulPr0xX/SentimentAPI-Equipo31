document.addEventListener("DOMContentLoaded", () => {
    const sentimentForm = document.getElementById("sentimentForm")
    const commentInput = document.getElementById("commentInput")
    const statusBadge = document.getElementById("statusBadge")
    const statusText = document.getElementById("statusText")
    const submitButton = document.getElementById("submitButton")

    sentimentForm.addEventListener("submit", async (e) => {
        e.preventDefault() // Evita que la página se recargue
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
            console.log("Resultado de IA:", result)
            alert(`Análisis completado: ${result.sentiment}`)
            commentInput.value = ""

        } catch (error) {
            console.error("Error:", error)
            alert("Hubo un problema al conectar con el servicio de IA.")
        } finally {
            setLoadingState(false)
        }
    })

    function setLoadingState(isLoading) {
        if (isLoading) {
            statusBadge.classList.add("sending")
            statusText.textContent = "Analizando..."
            submitButton.disabled = true
        } else {
            statusBadge.classList.remove("sending")
            statusText.textContent = "Esperando..."
            submitButton.disabled = false
        }
    }
    commentInput.addEventListener("keydown", (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key === "Enter") {
            sentimentForm.requestSubmit()
        }
    })
})