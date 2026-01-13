import streamlit as st
import pandas as pd
import os
from ClasificadorTexto import ClasificadorTexto

class AplicacionSentimientos:
    """Clase principal para la interfaz de Streamlit."""
    
    def __init__(self):
        st.set_page_config(page_title="Analizador IA Multilingüe")
        self.ruta_base = r'C:\Users\XxGho\OneDrive\Documentos\Documentos Javier\Datos varios de cursos\Hackaton\streamlit\Modelos'
        self.clasificadores = {}

    def ConfigurarModelos(self):
        """Inicializa los clasificadores para cada idioma."""
        with st.spinner("Cargando inteligencia artificial..."):
            # Configuración para Inglés
            self.clasificadores['English'] = ClasificadorTexto(
                os.path.join(self.ruta_base, 'modelo_sentimientoENG.pkl'),
                os.path.join(self.ruta_base, 'tfidf_vectorizerENG.pkl'),
                "Inglés"
            )
            # Configuración para Español
            self.clasificadores['Español'] = ClasificadorTexto(
                os.path.join(self.ruta_base, 'modelo_sentimientoESP.pkl'),
                os.path.join(self.ruta_base, 'tfidf_vectorizerESP.pkl'),
                "Español"
            )

    def EjecutarInterfaz(self):
        """Dibuja la interfaz de usuario."""
        st.title("Clasificador de Texto")
        st.markdown("Analiza la categoría de tu texto y descubre las palabras con mayor peso semántico.")
        
        idioma = st.radio("Selecciona el modelo:", ("Español", "English"), horizontal=True)
        texto_usuario = st.text_area(f"Entrada de texto ({idioma}):", placeholder="Escribe aquí...")

        if st.button("Ejecutar Análisis"):
            if not texto_usuario.strip():
                st.warning("Por favor, escribe un texto válido.")
                return

            # Obtener resultados
            analizador = self.clasificadores[idioma]
            res = analizador.AnalizarTexto(texto_usuario)

            # --- Mostrar Resultados Principales ---
            st.divider()
            col1, col2 = st.columns(2)
            col1.metric("Clase Detectada", res["prediccion"])
            col2.metric("Nivel de Confianza", f"{res['confianza']:.2%}")

            # --- Top 5 Palabras Influyentes ---
            st.subheader("Top 5 Palabras más Influyentes")
            if res["top_palabras"]:
                # Creamos un DataFrame para mostrarlo estético
                df_top = pd.DataFrame(res["top_palabras"], columns=["Palabra", "Importancia"])
                st.table(df_top)
            else:
                st.info("No se identificaron palabras clave con el vectorizador actual.")

            # --- Gráfico de Probabilidades ---
            st.subheader("Distribución de Probabilidad")
            st.bar_chart(res["df_probabilidades"].set_index("Clase"))
