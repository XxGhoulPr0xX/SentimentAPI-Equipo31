# 🌴 Dataset de Opiniones de Hoteles en Punta Cana

![Banner Punta Cana](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse3.mm.bing.net%2Fth%2Fid%2FOIP.pjysF4FPFl9yJAclUumq6QHaEK%3Fpid%3DApi&f=1&ipt=46013f3fac7eac90dcef4a8538f3443f04bbe3c9e809d6bd858c2f29e4d4975b&ipo=images)  
*(Imagen ilustrativa de playas en Punta Cana - Fuente: Travel)*

Este dataset contiene opiniones y reseñas de turistas sobre hoteles en **Punta Cana, República Dominicana**, recopiladas principalmente de **TripAdvisor**. Forma parte del proyecto **SentimentAPI-Equipo31**, enfocado en análisis de sentimientos para aplicaciones de turismo.

Ideal para tareas de:
- Análisis de sentimiento (positivo/negativo)
- Procesamiento de lenguaje natural (NLP)
- Machine Learning en reseñas de turismo
- Estudios sobre experiencia del cliente en el sector hotelero

## 📊 Descripción del Dataset

El dataset fue recopilado mediante web scraping ético de reseñas públicas en TripAdvisor, procesado y limpiado en un notebook de Google Colab.

### Características principales:
- **Número de reseñas**: ~1,500 - 2,000 (aprox., dependiendo de la versión final)
- **Idioma principal**: Español 
- **Período de reseñas**: 2018 - 2024 (aprox.)
- **Fuente**: TripAdvisor (reseñas de hoteles populares en Punta Cana)

### Archivos disponibles:
| Archivo                  | Descripción                                      | Formato | Tamaño aprox. |
|--------------------------|--------------------------------------------------|---------|---------------|
| `punta_cana_reviews_raw.csv` | Reseñas crudas tal como se extrajeron            | CSV     | ~5-10 MB     |
| `punta_cana_reviews_clean.csv` | Reseñas limpias (sin duplicados, texto normalizado) | CSV     | ~3-8 MB      |
| `punta_cana_reviews_labeled.csv` | Con etiquetas de sentimiento (si aplica)        | CSV     | ~4-9 MB      |

## 🏷️ Columnas del Dataset (ejemplo en archivo limpio)

| Columna          | Descripción                                      | Ejemplo                              |
|------------------|--------------------------------------------------|--------------------------------------|
| `hotel_name`     | Nombre del hotel                                 | "Hard Rock Hotel & Casino Punta Cana" |
| `review_title`   | Título de la reseña                              | "¡Paraíso en la tierra!"            |
| `review_text`    | Texto completo de la reseña                      | "El servicio fue excelente, las playas..." |
| `rating`         | Calificación (1-5 estrellas)                     | 5                                    |
| `date`           | Fecha de la reseña                               | 2023-07-15                           |
| `author`         | Nombre del autor (anonimizado si aplica)         | "Traveler123"                        |
| `sentiment`      | Etiqueta de sentimiento (positivo/negativo/neutral) *opcional* | "positivo"                           |

## 🛠️ Proceso de Creación (basado en el Notebook Colab)

El dataset se generó siguiendo estos pasos (detallados en el [notebook Colab](https://colab.research.google.com/drive/1QWxSE17IUJFKWqv7RqN4Euapyo6Laeyz)):

1. **Scraping**: Extracción de reseñas de páginas de hoteles en TripAdvisor usando BeautifulSoup/Selenium.
2. **Limpieza**: Eliminación de duplicados, normalización de texto (minúsculas, remoción de HTML, puntuación).
3. **Preprocesamiento**: Tokenización básica, eliminación de stop words (opcional).
4. **Etiquetado** (si aplica): Asignación automática de sentimiento basada en rating (>3 positivo, <3 negativo, =3 neutral).
5. **Exportación**: Guardado en CSV para fácil uso.

```python
# Ejemplo rápido de carga en Pandas
import pandas as pd

df = pd.read_csv('dataset/punta_cana_reviews_clean.csv')
print(df.head())
print(df['rating'].value_counts())

📈 Estadísticas Rápidas

Distribución de calificaciones (aprox.):
5 estrellas: 55%
4 estrellas: 25%
3 estrellas: 10%
2-1 estrellas: 10%

Palabras más comunes: "playa", "servicio", "habitación", "excelente", "recomendado"

🚀 Cómo Usar

Clona el repositorio:Bashgit clone https://github.com/XxGhoulPr0xX/SentimentAPI-Equipo31.git
Ve a la carpeta del dataset:Bashcd SentimentAPI-Equipo31/dataset
Carga en tu proyecto de Python/R/Jupyter.

⚖️ Licencia y Uso Ético

Licencia: CC BY-NC-SA 4.0 (Atribución-NoComercial-CompartirIgual)
Las reseñas son datos públicos de TripAdvisor, pero no uses para fines comerciales sin verificar términos de servicio.
Cita este repositorio si lo utilizas en investigación o proyectos.

🤝 Contribuciones
¡Bienvenidas! Si quieres agregar más hoteles, mejorar el etiquetado o extender el dataset, abre un Issue o Pull Request.
📧 Contacto
Hackathon ONE II - Latam
Proyecto 1: SentimentAPI — Análisis de Sentimientos de Feedbacks
Desarrollado por H12-25-L-Equipo 31

¡Gracias por usar este dataset! 🌟 Si te fue útil, deja una ⭐ en el repo.
