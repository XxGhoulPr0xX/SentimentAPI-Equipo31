# Clasificador de Texto Multilingüe (IA)

Este proyecto es una aplicación web desarrollada con **Streamlit** que utiliza modelos de Machine Learning (Scikit-Learn) para clasificar texto en español e inglés. La aplicación no solo predice la categoría, sino que también explica qué palabras influyeron más en la decisión de la IA.

## Funcionalidades

* **Detección de Clase**: Clasifica el texto ingresado según los modelos entrenados.
* **Análisis de Confianza**: Muestra la probabilidad exacta de la predicción.
* **Interpretabilidad (Top 5)**: Lista las 5 palabras del texto que tuvieron mayor peso matemático en el resultado.
* **Soporte Dual**: Selector dinámico para modelos en **Español** y **Inglés**.

---

## Requisitos e Instalación

### 1. Clonar o descargar el proyecto

Asegúrate de tener la siguiente estructura de archivos:

* `Main.py` (Punto de entrada)
* `AplicacionSentimientos.py` (Interfaz)
* `ClasificadorTexto.py` (Lógica de IA)
* `Modelos/` (Carpeta con tus archivos `.pkl`)

### 2. Crear y activar el Entorno Virtual

```bash
# Crear entorno
python -m venv env

# Activar (Windows)
.\env\Scripts\activate

# Activar (Mac/Linux)
source env/bin/activate

```
### 3. Instalación de Dependencias

Es necesario instalar las librerías listadas en el archivo de configuración incluido para asegurar el correcto funcionamiento del entorno.

Ejecuta el siguiente comando en la terminal del entorno virtual dentro de la carpeta del proyecto:

```bash
pip install -r requirements.txt
```

## Cómo Correr la Aplicación

Una vez que el entorno esté activo y las librerías instaladas, ejecuta el siguiente comando desde la raíz del proyecto:

```bash
python -m streamlit run Main.py

```

---

## Estructura del Código (Arquitectura)

El proyecto sigue un diseño orientado a objetos para facilitar su mantenimiento:

1. **`ClasificadorTexto`**: Clase encargada de cargar los archivos `.pkl` y procesar el texto mediante el vectorizador y el modelo.
2. **`AplicacionSentimientos`**: Clase que gestiona la interfaz visual, los botones y los gráficos de Streamlit.
3. **`Main`**: El disparador que orquestra la inicialización de los componentes.
