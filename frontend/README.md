# Frontend

Este proyecto fue generado con
[Angular CLI](https://github.com/angular/angular-cli) versión **20.3.13**.

## 📌 Resumen

El frontend está implementado en **Angular** y provee la interfaz para consumir
el servicio de análisis de sentimientos. Soporta:

- Análisis **individual** (comentario único).
- Análisis **masivo** mediante upload de `.csv` (columna seleccionada por el
  usuario).
- Visualizaciones (gráfico de torta), tablas de resultados, paginación y
  notificaciones.
- Modo **claro/oscuro** y persistencia de preferencias (`localStorage` y
  `sessionStorage`).

## ✅ Estado actual

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

## 🔧 Estructura y rutas principales

- `src/app/routes/inicio` — Componente principal (`Inicio`)
  - `inicio.ts`, `inicio.html`, `inicio.css`
- `src/app/core/services` — Servicios centrales
  - `sentiment-api-service.ts` — cliente HTTP (base por defecto:
    `http://localhost:5000`)
  - `theme-service.ts` — gestión de tema
- `src/app/shared/ui` — componentes reutilizables (header, footer,
  `grafico-pie`, `campo-seleccion`, `resultados-analisis`)
- `src/app/core/interfaces/sentiment-api.ts` — tipos `SentimentRequest` y
  `SentimentResponse`

## 🔗 Endpoints usados por el frontend

- Base por defecto: `http://localhost:5000` (definida en `SentimentApiService`)
- POST `/sentiment-api/analizar-comentario` — payload: `{ text }` → devuelve
  `SentimentResponse`
- POST `/sentiment-api/analizar-archivo` — `multipart/form-data` con campos
  `file` y `columnName` → devuelve `SentimentResponse[]`
- POST `/api/config/idioma?lang=<es|en>` — configura idioma de análisis
- GET `/stats/list` — obtiene lista de resultados almacenados

> Nota: Para cambiar la URL del backend, editar
> `private _url = 'http://localhost:5000'` en
> `src/app/core/services/sentiment-api-service.ts`.

## 🧪 Validaciones y comportamiento

- Texto individual: mínimo 10 caracteres y no puede ser solo espacios.
- Análisis masivo: se requiere un archivo implicando un CSV válido y que la
  columna indicada exista.
- Errores HTTP manejados con mensajes diferenciados (sin conexión, 400, 500,
  servicio no disponible).
- Sesión: la forma de análisis, el idioma y la pestaña seleccionada se guardan
  en `sessionStorage` para mejorar la UX.

## ⚙️ Comandos útiles

- Instalar dependencias: `pnpm install` o `npm install`
- Iniciar servidor de desarrollo: `pnpm run start` (usa `ng serve`)
- Build producción: `pnpm run build`
- Ejecutar tests unitarios: `pnpm run test`
- Linter (auto-fix): `pnpm run lint`

## 🧭 Notas para pruebas manuales

1. Asegúrate de que el backend esté corriendo y accesible en la URL configurada.
2. Ejecuta `pnpm run start` en el directorio `frontend`.
3. Navega a `http://localhost:4200` (por defecto) y prueba:
   - Análisis individual: escribe al menos 10 caracteres y envía.
   - Análisis masivo: sube un CSV con encabezado y usa el nombre de columna con
     texto para analizar.
   - Cambia idioma y verifica llamadas a `configurarIdioma`.

## 🧾 Dependencias principales

Revisa `package.json` para versiones y scripts. Entre las dependencias clave:

- `@angular/*` (v20.3.x)
- `@angular/material` (v20.2.x)
- `ngx-echarts` / `echarts` para gráficos
