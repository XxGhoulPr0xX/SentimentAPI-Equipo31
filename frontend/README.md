# Frontend

Este proyecto fue generado con
[Angular CLI](https://github.com/angular/angular-cli) versión **20.3.13**.

## Información y estado actual del directorio `frontend`

**Resumen:** El frontend está implementado en **Angular** y ofrece la interfaz
para consumir el servicio de análisis de sentimientos. La aplicación soporta
análisis individual y masivo (archivo), visualizaciones, gestión de tema y
notificaciones.

### Componentes y rutas principales 🔧

- `src/app/routes/inicio` — Componente `Inicio`: interfaz principal para el
  análisis de sentimientos (modo **individual** y **masivo**), selección de
  idioma, carga de archivos, tablas de resultados, paginación, gráfico circular
  y notificaciones.
- `src/app/shared/ui` — Componentes reutilizables: `header`, `footer`,
  `grafico-pie`, `campo-seleccion`, `resultados-analisis`.

### Servicios y lógica central 🧠

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

### Funcionalidades implementadas ✅

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

### Integración y endpoints ⚠️

- Base por defecto: `http://localhost:5000`.
- Endpoints usados por el frontend:
  - `POST /sentiment-api/analizar-comentario` — { text }
  - `POST /sentiment-api/analizar-archivo` — multipart/form-data (`file`,
    `columnName`)
  - `POST /api/config/idioma?lang=<es|en>` — cambia el idioma de análisis
- Asegúrate de levantar el backend antes de realizar pruebas locales.

### Ejecución y pruebas 🧩

- Instalar dependencias: `pnpm install` o `npm install`.
- Ejecutar servidor de desarrollo: `pnpm run start` (usa `ng serve`).
- Compilar para producción: `pnpm run build`.
- Ejecutar tests unitarios: `pnpm run test`.

### Archivos y rutas de interés 📁

- `src/app/routes/inicio` — UI y lógica principal de análisis (`inicio.ts`,
  `inicio.html`).
- `src/app/core/services/sentiment-api-service.ts` — cliente HTTP y endpoints.
- `src/app/core/services/theme-service.ts` — gestión de tema claro/oscuro.
- `src/app/shared/ui` — componentes reutilizables (header, footer, gráfico,
  resultados).
