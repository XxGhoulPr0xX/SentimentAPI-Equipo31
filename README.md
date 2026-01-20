# SentimentAPI-Equipo31

Este proyecto fue desarrollado con fines demostrativos.

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

```mermaid
flowchart LR
    A[Usuario escribe comentario] --> B[Frontend HTML/JS]
    B -->|POST /sentiment| C[SentimentController]
    C --> D[SentimentService]
    D -->|Ejecuta inferencia| E[Modelo ONNX]
    E -->|Resultado| D
    D --> C
    C -->|JSON Response| B
```

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

**API de Análisis:**

- `POST /sentiment-api/analizar-comentario`: Recibe un JSON `{ text: string }`. Retorna `SentimentResponse`.
- `POST /sentiment-api/analizar-archivo`: Recibe `multipart/form-data` con `file` (.csv) y `columnName`. Retorna una lista de `SentimentResponse`.

**API de Configuración:**

- `POST /api/config/idioma`: Recibe el parámetro `lang` (ej. "es", "en") para cambiar el contexto del `SentimentService`.

**API de Estadísticas:**

- `GET /stats/list`: Retorna el historial completo de análisis en JSON e imprime un reporte detallado en la consola del servidor.

**Manejo de Errores:**

- Implementación de `ApiExceptionHandler` para capturar `MethodArgumentNotValidException` y devolver respuestas `400 Bad Request` estructuradas cuando fallan las validaciones de entrada.

## Ejecución y pruebas del Backend

### Dependencias y versiones relevantes

- **Java JDK 17 o superior** (recomendado para Spring Boot 4.x)
- **Maven 3.9+** (o usar el wrapper `mvnw`)
- **ONNX Runtime 1.19.0**
- **h2database**
- **opencsv 5.9**

### Ejecución del servidor de desarrollo

Para levantar el backend en modo desarrollo:

```bash
mvn spring-boot:run
```

El servidor se iniciará por defecto en:

```
http://localhost:8080
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
  "prevision": "Positivo",
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
  "prevision": "Negativo",
  "probabilidad": 0.93
}
```

---

### Mensajes de error implementados

| Código | Mensaje                | Descripción                  |
| ------ | ---------------------- | ---------------------------- |
| 0      | No se pudo conectar    | Backend caído o sin internet |
| 400    | Solicitud inválida     | Datos mal formados           |
| 500    | Error interno          | Problema en el servidor      |
| 503    | Servicio no disponible | Servicio temporalmente caído |

### Base de datos en memoria (H2)

Durante la ejecución:

- La base de datos **H2** se inicializa automáticamente en memoria.
- Los datos se pierden al reiniciar el servidor.

### Demostración funcional

La API puede probarse mediante:

- **cURL**
- **Interfaz web Angular**

### Ejemplo con cURL

```bash
curl -X POST http://localhost:8080/sentiment-api/analizar-comentario -H "Content-Type: application/json" -d "{\"text\": \"Excelente servicio, la atención fue rápida y la comida estaba deliciosa. ¡Muy recomendado!\"}"
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

## Resumen

El frontend está implementado en **Angular** y provee la interfaz para consumir
el servicio de análisis de sentimientos. Soporta:

- Análisis **individual** (comentario único).
- Análisis **masivo** mediante upload de `.csv` (columna seleccionada por el
  usuario).
- Visualizaciones (gráfico de torta), tablas de resultados, paginación y
  notificaciones.
- Modo **claro/oscuro** y persistencia de preferencias (`localStorage` y
  `sessionStorage`).

## Estado actual

- Interfaz principal (`Inicio`) con:
  - Formulario para análisis individual (validación: mínimo 10 caracteres).
  - Formulario para análisis masivo (subida de CSV + `columnName`).
  - Resultados mostrados en tabla y gráfico (`grafico-pie`).
  - Paginación y eliminación de registros.
  - Notificaciones con `MatSnackBar`.
  - Manejo de errores con mensajes amigables para el usuario.
- Servicios centrales:
  - `SentimentApiService` con endpoints para análisis y configuración de idioma.
  - `ThemeService` que persiste y aplica el tema claro/oscuro.
- Persistencia ligera en el cliente:
  - Preferencia de forma de análisis e idioma en `sessionStorage`.
  - Tema en `localStorage`.
- Soporte para cargar y analizar archivos CSV y visualizar un listado de
  resultados históricos (consulta a `/stats/list`).

## Estructura y rutas principales

- `src/app/routes/inicio` — Componente principal (`Inicio`)
  - `inicio.ts`, `inicio.html`, `inicio.css`
- `src/app/core/services` — Servicios centrales
  - `sentiment-api-service.ts` — cliente HTTP (base por defecto:
    `http://localhost:8080`)
  - `theme-service.ts` — gestión de tema
- `src/app/shared/ui` — componentes reutilizables (header, footer,
  `grafico-pie`, `campo-seleccion`, `resultados-analisis`)
- `src/app/core/interfaces/sentiment-api.ts` — tipos `SentimentRequest` y
  `SentimentResponse`

## Endpoints usados por el frontend

- Base por defecto: `http://localhost:8080` (definida en `SentimentApiService`)
- POST `/sentiment-api/analizar-comentario` — payload: `{ text }` → devuelve
  `SentimentResponse`
- POST `/sentiment-api/analizar-archivo` — `multipart/form-data` con campos
  `file` y `columnName` → devuelve `SentimentResponse[]`
- POST `/api/config/idioma?lang=<es|en>` — configura idioma de análisis
- GET `/stats/list` — obtiene lista de resultados almacenados

> Nota: Para cambiar la URL del backend, editar
> `private _url = 'http://localhost:8080'` en
> `src/app/core/services/sentiment-api-service.ts`.

## Validaciones y comportamiento

- Texto individual: mínimo 10 caracteres y no puede ser solo espacios.
- Análisis masivo: se requiere un archivo implicando un CSV válido y que la
  columna indicada exista.
- Errores HTTP manejados con mensajes diferenciados (sin conexión, 400, 500,
  servicio no disponible).
- Sesión: la forma de análisis, el idioma y la pestaña seleccionada se guardan
  en `sessionStorage` para mejorar la UX.


---

# Despliegue con Docker

El proyecto incluye soporte para **Docker** y **Docker Compose**, lo que facilita la orquestación de los servicios de backend y frontend.

### Docker Compose

Para levantar toda la infraestructura (Backend + Frontend) de manera simplificada, utiliza el archivo `docker-compose.yml`:

```bash
# Para construir y levantar los contenedores
docker-compose up --build

```

---

# Pruebas y Aseguramiento de Calidad

Para garantizar la estabilidad y el correcto funcionamiento del núcleo de procesamiento, se ha implementado una suite completa de pruebas automatizadas utilizando **JUnit 5** y **Spring Boot Test**.

## Pruebas Unitarias (Unit Testing)
Se valida la lógica de los componentes de forma aislada, asegurando que cada pieza cumpla con su responsabilidad única.

- **Validación de DTOs (`SentimentRequestTest`):**
  - Verificación de restricciones de entrada (JSR-303).
  - Comprobación de casos borde: textos nulos, vacíos, solo espacios en blanco o longitud inferior a la permitida (3 caracteres).
- **Entidades (`SentimentRecordTest` & `SentimentResponseTest`):**
  - Verificación de constructores, getters y setters.
  - Consistencia en la generación de fechas (`createdAt`) y manejo de probabilidades.
- **Persistencia (`SentimentRecordRepositoryTest`):**
  - Pruebas sobre la base de datos en memoria H2 utilizando `@DataJpaTest`.
  - Validación de operaciones CRUD (Guardar, Buscar por ID, Contar por predicción, Eliminar).

### Pruebas de Integración (Integration Testing)
Se valida la interacción entre las distintas capas del sistema (Controlador → Servicio → Repositorio) utilizando `MockMvc` para simular peticiones HTTP reales.

- **Controlador (`SentimentControllerIntegrationTest`):**
  - **Flujo Exitoso:** Verifica que una petición `POST` válida retorne estado `200 OK`, la estructura JSON correcta y persista el registro en la base de datos.
  - **Manejo de Errores:** Verifica que peticiones inválidas (texto vacío o muy corto) retornen estado `400 Bad Request`.
  - **Integridad de Datos:** Asegura que la respuesta enviada al cliente coincida exactamente con lo almacenado en la base de datos.
  - **Casos Complejos:** Pruebas con textos largos, caracteres especiales y múltiples peticiones secuenciales.

### Ejecución de las pruebas

Para ejecutar la suite completa de pruebas y verificar el reporte de resultados, utiliza el siguiente comando en el directorio del backend:

```bash
mvn test
```