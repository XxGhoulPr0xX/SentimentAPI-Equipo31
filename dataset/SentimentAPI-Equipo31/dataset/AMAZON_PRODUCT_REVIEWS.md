#📊 Sentiment API AMAZON PRODUCT REVIEWS – Proyecto Grupo 31

Este proyecto implementa un **modelo de análisis de sentimientos** aplicado a reseñas de productos de Amazon.  
El objetivo es construir un **MVP (Minimum Viable Product)** que permita clasificar comentarios en **Positivos (1)** y **Negativos (0)**, 
integrando técnicas de **Data Science** y preparación para una futura API.

---

##🚀 Objetivo del Proyecto
- Automatizar la clasificación de reseñas de clientes.
- Detectar tempranamente comentarios negativos para mejorar la atención al cliente.
- Proporcionar una base sólida para integrar el modelo en una API de análisis de sentimientos.

---

## 🛠️ Preparación de los Datos
### 📦 Importación de Bibliotecas
Se utilizan librerías clave para el procesamiento y modelado:
- **pandas, numpy, re** → manejo y limpieza de datos.
- **scikit-learn** → vectorización de texto y entrenamiento del modelo.
- **matplotlib, seaborn** → visualización de métricas.
- **joblib** → serialización del modelo entrenado.

---

### 📌 Extracción del Dataset
- Fuente: **Amazon Product Reviews**.  
- Columnas relevantes: `review_headline`, `review_body`, `sentiment`.  
- Formato esperado:  
  - `text` → comentario completo.  
  - `sentiment` → etiqueta binaria (0 = Negativo, 1 = Positivo).  

Ejemplo de carga inicial:
```python
dts = pd.read_csv("Amazon-Product-Reviews.csv")
dts.head()

---

### 📝 Limpieza de los Datos
Se seleccionan únicamente las columnas necesarias de los 30,846 registros / 16 columnas originales:

Ejemplo del proceso: 
```python
dts2 = dts[["review_body", "sentiment"]].copy()

Resultado:
Dataset reducido: 30,846 registros / 2 columnas.

---

### 🧩 Unión de Columnas
Se crea una nueva columna text concatenando título y cuerpo de la reseña:

El proceso llevado a cabo fue el siguiente, ayudando a simplificar la estructura y 
permite qe el modelo lea un texto completo como lo haría una persona:

```python
dts2["text"] = dts2["review_body"]
dts2 = dts2[["text", "sentiment"]]


