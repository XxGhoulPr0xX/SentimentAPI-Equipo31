# Frontend

Este proyecto fue generado con
[Angular CLI](https://github.com/angular/angular-cli) versión **20.3.13**.

## Servidor de desarrollo

Para iniciar un servidor de desarrollo local, ejecuta:

```bash
ng serve
```

Una vez arrancado, abre tu navegador en `http://localhost:4200/`. La aplicación
se recargará automáticamente cuando modifiques los archivos fuente.

## Información y estado actual del directorio `frontend`

**Resumen:** El frontend está implementado en Angular y proporciona la interfaz
para consumir el servicio de análisis de sentimientos. A continuación se
describen los puntos más relevantes de lo ya realizado.

### Componentes y rutas principales 🔧

- `src/app/routes/inicio` — Componente `Inicio`: interfaz principal para el
  análisis de sentimientos (modo **individual** y **masivo**), tablas,
  paginación, gráfico circular y mensajes de notificación.
- `src/app/shared/ui` — Componentes reutilizables: `header`, `footer`,
  `grafico-pie`, `campo-seleccion`.

### Servicios y lógica central 🧠

- `src/app/core/services/sentiment-api-service.ts` — `SentimentApiService`:
  realiza llamadas POST a la ruta `/sentiment` del backend (por defecto
  `http://localhost:5000`).
- `src/app/core/services/theme-service.ts` — `ThemeService`: gestiona el modo
  claro/oscuro y lo persiste en `localStorage`.
- `src/app/core/interfaces/sentiment-api.ts` — Tipos TypeScript para las
  solicitudes y respuestas del API.

### Funcionalidades implementadas ✅

- Análisis **individual** de texto con validaciones y visualización del
  resultado en tabla.
- Análisis **masivo** (prototipo): carga de CSV y resumen estadístico
  (actualmente usa un ejemplo mock al procesar archivos; es un punto a completar
  para integrar con el backend si se desea).
- Visualización de la distribución de sentimientos mediante un gráfico de pastel
  y tablas con paginación.
- Modo claro/oscuro persistente por usuario.
- Notificaciones de estado con `MatSnackBar`.

### Integración y requisitos ⚠️

- El frontend asume que la API de sentimiento está disponible en
  `http://localhost:5000` y expone el endpoint `POST /sentiment`.
- Para pruebas locales, asegúrate de iniciar también el backend.

### Comandos útiles 🧩

- Instalar dependencias: `pnpm install` o `npm install`.
- Ejecutar servidor: `ng serve`.
- Compilar para producción: `ng build`.
- Ejecutar tests unitarios: `ng test`.

### Archivos y rutas de interés 📁

- `src/app/routes/inicio` — UI y lógica principal de análisis.
- `src/app/core/services/sentiment-api-service.ts` — Cliente HTTP.
- `src/app/core/services/theme-service.ts` — Gestión de tema.
- `src/app/shared/ui` — Componentes reutilizables (header, footer, gráfico,
  etc.).

> Nota: El análisis masivo está parcialmente implementado.
