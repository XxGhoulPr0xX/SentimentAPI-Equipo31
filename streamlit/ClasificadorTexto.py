import streamlit as st
import pandas as pd
import joblib

class ClasificadorTexto:
    """Clase para gestionar la carga y predicción de modelos de IA."""
    
    def __init__(self, ruta_modelo, ruta_vectorizador, nombre_idioma):
        self.ruta_modelo = ruta_modelo
        self.ruta_vectorizador = ruta_vectorizador
        self.nombre_idioma = nombre_idioma
        self.modelo = None
        self.vectorizador = None
        self.CargarRecursos()

    def CargarRecursos(self):
        """Carga los archivos .pkl del disco."""
        try:
            self.modelo = joblib.load(self.ruta_modelo)
            self.vectorizador = joblib.load(self.ruta_vectorizador)
        except Exception as e:
            st.error(f"Error al cargar recursos de {self.nombre_idioma}: {e}")

    def AnalizarTexto(self, texto):
        """Realiza la predicción y encuentra las 5 palabras más influyentes."""
        # 1. Transformación
        texto_vectorizado = self.vectorizador.transform([texto])
        
        # 2. Predicción y Probabilidades
        prediccion = self.modelo.predict(texto_vectorizado)[0]
        probabilidades = self.modelo.predict_proba(texto_vectorizado)[0]
        clases = self.modelo.classes_
        
        indice_clase = list(clases).index(prediccion)
        puntaje_confianza = probabilidades[indice_clase]

        # 3. Cálculo de las 5 palabras más influyentes
        nombres_palabras = self.vectorizador.get_feature_names_out()
        lista_top_palabras = []
        
        if hasattr(self.modelo, "coef_"):
            # Obtener pesos para la clase predicha
            pesos = self.modelo.coef_[0] if len(clases) <= 2 else self.modelo.coef_[indice_clase]
            
            # Índices de palabras presentes en el texto ingresado
            indices_presentes = texto_vectorizado.nonzero()[1]
            
            if len(indices_presentes) > 0:
                # Crear lista de (palabra, peso)
                palabras_con_pesos = [(nombres_palabras[i], pesos[i]) for i in indices_presentes]
                # Ordenar por peso de forma descendente y tomar las 5 primeras
                palabras_con_pesos.sort(key=lambda x: x[1], reverse=True)
                lista_top_palabras = palabras_con_pesos[:5]
        
        return {
            "prediccion": str(prediccion).upper(),
            "confianza": puntaje_confianza,
            "top_palabras": lista_top_palabras,
            "df_probabilidades": pd.DataFrame({"Clase": clases, "Probabilidad": probabilidades})
        }

