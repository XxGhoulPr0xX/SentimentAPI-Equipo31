# SentimentAPI-Equipo31

# Backend

Este proyecto fue generado con
[Spring Boot](https://github.com/spring-projects/spring-boot) versión **4.0.1**.

Este proyecto está implementado en **Spring Boot** y Java. Actúa como el núcleo de procesamiento, encargándose de la carga de modelos de Inteligencia Artificial (ONNX), la recepción y analisis de textos con el modelo de ia,la gestión de archivos CSV y la persistencia de datos en una base de datos en memoria (H2).

## Informacion y estado actual del directorio backend

**Resumen:** El backend expone una API REST para el análisis de sentimientos, gestiona la lógica de inferencia utilizando `onnxruntime` y ofrece servicios de utilidad para el procesamiento de archivos y reportes estadísticos en consola.

### Componentes y controladores principales

- `com.equipo31.app.Controller.SentimentController`: Controlador REST principal. Gestiona las peticiones de análisis de texto individual y procesamiento masivo de archivos. Interactúa con la capa de persistencia para guardar el historial.
- `com.equipo31.app.Controller.ApiConfig`: Controlador de configuración. Expone endpoints para modificar el comportamiento del servicio en tiempo real, específicamente el cambio de idioma del modelo.
- `com.equipo31.app.Controller.StatsController`: Controlador administrativo para consultar y visualizar los registros almacenados en la base de datos a través de la consola del servidor y respuestas JSON.
- `com.equipo31.app.Controller.index` y `aboutit`: Controladores de vistas que gestionan el enrutamiento hacia las plantillas Thymeleaf (`index`, `aboutit`).

### Arquitectura del flujo de datos

```text
Usuario → Frontend → API REST (Spring Boot) → Modelo ONNX → Predicción → Respuesta JSON
```

1. El usuario envía un texto o archivo CSV.
2. El controlador REST recibe la petición.
3. El servicio de IA ejecuta la inferencia usando el modelo ONNX cargado.
4. Se procesa la salida del modelo (probabilidades).
5. Se retorna la predicción y se persiste el resultado.


### Servicios y logica central

- `com.equipo31.app.Service.SentimentService`: Servicio central de IA.
- Carga modelos ONNX (`.onnx`) desde el classpath copiándolos a archivos temporales para su lectura.
- Gestiona sesiones concurrentes para modelos en español e inglés.
- Ejecuta la inferencia sobre tensores de entrada y procesa la salida (probabilidades y etiquetas).

- `com.equipo31.app.Service.AnalizarArchivoService`: Servicio de utilidad para archivos.
- Utiliza `OpenCSV` para leer flujos de entrada.
- Extrae dinámicamente columnas específicas de archivos CSV basándose en el nombre de la cabecera.

- `com.equipo31.app.Service.EstadisticasRegistros`: Servicio de reporte. Genera tablas formateadas en los logs del sistema para visualizar el estado actual de la base de datos H2.

### Funcionalidades implementadas

- **Carga de Modelos ONNX:** Inicialización automática (`@PostConstruct`) de modelos de análisis de sentimiento para español e inglés utilizando `onnxruntime`.
- **Análisis de Texto:** Endpoint para recibir texto plano, validarlo y devolver una predicción de sentimiento (Positivo/Negativo) junto con su probabilidad.
- **Procesamiento de CSV:** Capacidad para recibir archivos `MultipartFile`, localizar una columna específica por nombre y ejecutar análisis en lote sobre cada fila válida.
- **Cambio Dinámico de Idioma:** Permite cambiar el modelo de inferencia activo (entre `es` y `en`) sin necesidad de reiniciar el servidor.
- **Persistencia y Auditoría:** Guardado automático de todas las predicciones (texto, etiqueta, probabilidad, fecha) en una base de datos H2 a través de `SentimentRecordRepository`.

### Integracion y endpoints

- **API de Análisis:**
- `POST /sentiment-api/analizar-comentario`: Recibe un JSON `{ text: string }`. Retorna `SentimentResponse`.
- `POST /sentiment-api/analizar-archivo`: Recibe `multipart/form-data` con `file` (.csv) y `columnName`. Retorna una lista de `SentimentResponse`.

- **API de Configuración:**
- `POST /api/config/idioma`: Recibe el parámetro `lang` (ej. "es", "en") para cambiar el contexto del `SentimentService`.

- **API de Estadísticas:**
- `GET /stats/list`: Retorna el historial completo de análisis en JSON e imprime un reporte detallado en la consola del servidor.

- **Manejo de Errores:**
- Implementación de `ApiExceptionHandler` para capturar `MethodArgumentNotValidException` y devolver respuestas `400 Bad Request` estructuradas cuando fallan las validaciones de entrada.

## Ejecución y pruebas del Backend

### Requisitos previos

- **Java JDK 17 o superior** (recomendado para Spring Boot 4.x)
- **Maven 3.9+** (o usar el wrapper `mvnw`)
- Sistema operativo: Windows, Linux o macOS


### Ejecución del servidor de desarrollo

Para levantar el backend en modo desarrollo:

```bash
mvn spring-boot:run
```

El servidor se iniciará por defecto en:

```
http://localhost:5000
```

## Uso de la API de análisis de sentimientos

### Endpoint principal

**POST** `/sentiment-api/analizar-comentario`

#### Ejemplo de petición

```json
{
  "text": "This product is amazing, I love it so much!"
}
```

#### Ejemplo de respuesta

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9938
}
```

---

## Ejemplos de pruebas del modelo

### Pruebas en inglés

#### Comentario positivo

**Entrada:**

```json
"This product is amazing, I love it so much!"
```

**Respuesta:**

```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9938
}
```

---

#### Comentario negativo

**Entrada:**

```json
"This is terrible, I hate this product. Very disappointed."
```

**Respuesta:**

```json
{
  "prevision": "Negativo",
  "probabilidad": 0.9914
}
```

---

### Pruebas en español

#### Reseña positiva

**Entrada:**

```json
"El hotel fue genial, la playa hermosa y el personal muy amable. ¡Volvería sin duda!"
```

**Respuesta:**

```json
{
  "prevision": "POSITIVO",
  "probabilidad": 0.84
}
```
---

#### Reseña negativa

**Entrada:**
```json
"Habitación sucia, comida mala y mucho ruido por la noche. Muy decepcionado."
```

**Respuesta:**

```json
{
  "prevision": "NEGATIVO",
  "probabilidad": 0.93
}
```

---

### Base de datos en memoria (H2)

Durante la ejecución:

- La base de datos **H2** se inicializa automáticamente en memoria.
- Los datos se pierden al reiniciar el servidor.

### Demostración funcional

La API puede probarse mediante:

* **Postman**
* **cURL**
* **Interfaz web Angular**

### Ejemplo con cURL

```bash
curl -X POST http://localhost:5000/sentiment-api/analizar-comentario \
-H "Content-Type: application/json" \
-d "{\"text\":\"I really like this application\"}"
```

### ¿Cómo el modelo llega a la predicción?

1. El texto de entrada se transforma en un tensor compatible con el modelo ONNX.
2. El modelo procesa el texto mediante una red neuronal entrenada para análisis de sentimiento.
3. La salida es un vector de probabilidades.
4. Se selecciona la clase con mayor probabilidad (`Positivo` o `Negativo`).
5. El resultado se retorna al cliente y se guarda en la base de datos H2.

### Notas importantes

- Los modelos **ONNX** se cargan automáticamente al iniciar la aplicación mediante `@PostConstruct`.
- No es necesario configurar manualmente la base de datos.
- El backend debe estar en ejecución antes de iniciar el frontend Angular.

### Archivos y rutas de interes

- `src/main/java/com/equipo31/app/Service/SentimentService.java`: Lógica de carga de ONNX y ejecución de tensores.
- `src/main/java/com/equipo31/app/Controller/SentimentController.java`: Puntos de entrada de la API REST.
- `src/main/resources/static/models/`: Ubicación de los archivos `.onnx` pre-entrenados.
- `src/main/java/com/equipo31/app/entity/SentimentRecord.java`: Definición de la entidad DB para persistencia.

# Frontend

Este proyecto fue generado con
[Angular CLI](https://github.com/angular/angular-cli) versión **20.3.13**.

## Información y estado actual del directorio `frontend`

**Resumen:** El frontend está implementado en **Angular** y ofrece la interfaz
para consumir el servicio de análisis de sentimientos. La aplicación soporta
análisis individual y masivo (archivo), visualizaciones, gestión de tema y
notificaciones.

### Componentes y rutas principales

- `src/app/routes/inicio` — Componente `Inicio`: interfaz principal para el
  análisis de sentimientos (modo **individual** y **masivo**), selección de
  idioma, carga de archivos, tablas de resultados, paginación, gráfico circular
  y notificaciones.
- `src/app/shared/ui` — Componentes reutilizables: `header`, `footer`,
  `grafico-pie`, `campo-seleccion`, `resultados-analisis`.

### Servicios y lógica central

- `src/app/core/services/sentiment-api-service.ts` — `SentimentApiService`:
  cliente HTTP que apunta por defecto a `http://localhost:5000` y expone métodos
  para:
  - `analizarComentario(body)` → POST `/sentiment-api/analizar-comentario`
  - `analizarArchivo(formData)` → POST `/sentiment-api/analizar-archivo`
    (multipart/form-data)
  - `configurarIdioma(param)` → POST `/api/config/idioma` (envía `lang` como
    query param)
- `src/app/core/services/theme-service.ts` — `ThemeService`: gestiona el modo
  claro/oscuro y lo persiste en `localStorage`.
- `src/app/core/interfaces/sentiment-api.ts` — Tipos TypeScript para las
  solicitudes y respuestas del API (`SentimentRequest`, `SentimentResponse`).

### Funcionalidades implementadas

- Análisis **individual** de texto: validaciones (mínimo 10 caracteres) y
  visualización del resultado en la tabla y gráfico.
- Análisis **masivo**: carga de archivos (`.csv`), envío multipart/form-data con
  campos `file` y `columnName`, y visualización de la lista de resultados (array
  de `SentimentResponse`).
- Selección de idioma para análisis y actualización mediante `configurarIdioma`.
- Visualización de la distribución de sentimientos con `grafico-pie` y tablas
  con paginación y eliminación de registros.
- Modo claro/oscuro persistente por usuario y notificaciones de estado con
  `MatSnackBar`.
- Guardado de la preferencia de forma de análisis en `sessionStorage`.

### Integración y endpoints

- Base por defecto: `http://localhost:5000`.
- Endpoints usados por el frontend:
  - `POST /sentiment-api/analizar-comentario` — { text }
  - `POST /sentiment-api/analizar-archivo` — multipart/form-data (`file`,
    `columnName`)
  - `POST /api/config/idioma?lang=<es|en>` — cambia el idioma de análisis
- Asegúrate de levantar el backend antes de realizar pruebas locales.

### Ejecución y pruebas

- Instalar dependencias: `pnpm install` o `npm install`.
- Ejecutar servidor de desarrollo: `pnpm run start` (usa `ng serve`).
- Compilar para producción: `pnpm run build`.
- Ejecutar tests unitarios: `pnpm run test`.

### Archivos y rutas de interés

- `src/app/routes/inicio` — UI y lógica principal de análisis (`inicio.ts`,
  `inicio.html`).
- `src/app/core/services/sentiment-api-service.ts` — cliente HTTP y endpoints.
- `src/app/core/services/theme-service.ts` — gestión de tema claro/oscuro.
- `src/app/shared/ui` — componentes reutilizables (header, footer, gráfico,
  resultados).
