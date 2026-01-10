# Documentación de Aportes de Augusto Paz- Proyecto SentimentAPI

> **Equipo 31 - Hackathon Latinoamérica**

---

# PARTE 1: Integración del Modelo ONNX

> Documentación de todo lo que hice para conectar el modelo de IA con el backend

---

## Resumen

Me encargué de integrar el modelo ONNX que entrenaron los chicos con el backend Spring Boot. Básicamente, el sistema ahora puede recibir un comentario, analizarlo con el modelo de Machine Learning, y devolver si es positivo o negativo con un porcentaje de confianza.

---

## Arquitectura

```mermaid
flowchart LR
    A[Usuario escribe comentario] --> B[Frontend HTML/JS]
    B -->|POST /sentiment| C[SentimentController]
    C --> D[SentimentService]
    D -->|Ejecuta inferencia| E[Modelo ONNX]
    E -->|Resultado| D
    D --> C
    C -->|JSON Response| B
    B -->|Muestra resultado| F[Badge con color]
```

---

## Lo que tuve que modificar

### 1. pom.xml - Agregar ONNX Runtime

Lo primero fue agregar la librería de Microsoft para poder ejecutar modelos ONNX en Java.

```xml
<!-- Esta es la librería que permite ejecutar el modelo -->
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId>
    <version>1.19.0</version>
</dependency>
```

**Importante:** Tuve que usar la versión 1.19.0 porque el modelo tiene IR version 10. Con la 1.16.3 tiraba error de versión incompatible.

---

### 2. SentimentService.java (archivo nuevo)

Este es el archivo principal que creé. Se encarga de:
- Cargar el modelo ONNX cuando arranca la app
- Recibir el texto y pasarlo al modelo
- Interpretar lo que devuelve el modelo

Lo más importante del código:

```java
// El modelo espera un array 2D, no 1D. Me costó darme cuenta de esto.
String[][] inputData = new String[][] {{ texto }};
OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);

// Ejecutar el modelo
OrtSession.Result resultado = session.run(inputs);
```

---

### 3. SentimentController.java

Antes el controlador devolvía siempre "Neutro" con 0.50 (estaba hardcodeado). Lo cambié para que use mi servicio:

```java
// Ahora llama al servicio que ejecuta el modelo
Map<String, Object> resultado = sentimentService.analizarSentimiento(req.getText());
String label = (String) resultado.get("etiqueta");
double prob = (Double) resultado.get("probabilidad");
```

---

### 4. application.properties

Agregué la ruta donde está el modelo:

```properties
onnx.model.path=models/modelo_sentimientoENG.onnx
```

---

### 5. index.js

Tuve que arreglar varias cosas:

1. **El JSON estaba mal leído** - El código buscaba `result.sentiment` pero el backend devuelve `prevision`

2. **Mostrar el resultado en el badge** - Antes usaba un `alert()`, ahora se muestra en el badge con colores

3. **Problema con Brave** - En Brave el formulario se enviaba como GET y el texto aparecía en la URL. Lo arreglé cambiando de `submit` a `click`:

```javascript
// Uso click en vez de submit porque Brave hacía que se enviara como GET
submitButton.addEventListener("click", async () => {
```

---

### 6. index.html

Cambié el botón de `type="submit"` a `type="button"` para evitar el problema de Brave:

```html
<!-- Puse type="button" porque en Brave se enviaba el form como GET -->
<button type="button" class="submit-button" id="submitButton">
```

---

### 7. index.css

Agregué los estilos para que el badge cambie de color según el resultado:

```css
.status-badge.positivo {
    background: rgba(34, 197, 94, 0.2);
    border-color: rgb(34, 197, 94);
    color: rgb(34, 197, 94);
}

.status-badge.negativo {
    background: rgba(239, 68, 68, 0.2);
    border-color: rgb(239, 68, 68);
    color: rgb(239, 68, 68);
}
```

---

## Problemas que tuve que resolver

### 1. Versión de ONNX incompatible
- **Error:** `Unsupported model IR version: 10, max supported IR version: 9`
- **Solución:** Actualizar de 1.16.3 a 1.19.0

### 2. El tensor tenía que ser 2D
- **Error:** `Invalid rank for input: string_input Got: 1 Expected: 2`
- **Solución:** Cambiar `new String[]{texto}` por `new String[][]{{texto}}`

### 3. Brave enviaba el form como GET
- **Problema:** El texto aparecía en la URL y no llegaba al servidor
- **Solución:** Usar `type="button"` y evento `click` en vez de `submit`

---

## Pruebas que hice

### Comentario positivo
```json
Entrada: "This product is amazing, I love it so much!"
Respuesta: {"prevision": "Positivo", "probabilidad": 0.9938}
```
El modelo dió 99.4% de confianza.

### Comentario negativo
```json
Entrada: "This is terrible, I hate this product. Very disappointed."
Respuesta: {"prevision": "Negativo", "probabilidad": 0.9914}
```
El modelo dió 99.1% de confianza.

---

## Cómo ejecutar

```powershell
cd app
./mvnw.cmd spring-boot:run
```

Después abrir: **http://localhost:5000/index**

---

## Notas

- El modelo está entrenado en inglés, así que funciona mejor con comentarios en ese idioma
- Es un modelo binario (Positivo/Negativo), no hay clasificación "Neutro" real
- Los resultados se guardan en la base de datos H2

---
---

# PARTE 2: LOG DE ERRORES

> **Fecha:** 5 de Enero de 2026
> 
> Implementaciones de mejora de UX, manejo de errores y validaciones

---

## Resumen de Aportes

Trabajé en mejorar la experiencia de usuario del frontend Angular y en robustecer el manejo de errores tanto en frontend como backend.

---

## 1. Mejoras de UX en el Footer

### Enlaces de miembros del equipo
- Los nombres ahora son enlaces clickeables a perfiles de LinkedIn/GitHub
- Si un miembro no tiene perfil, se muestra como texto plano (sin link roto)
- Links abren en pestaña nueva (`target="_blank"`)

### Efecto hover en enlaces activos
- Color primario al pasar el mouse
- Leve escalado (`transform: scale(1.05)`)
- Texto en negrita

**Archivos modificados:**
- `frontend/src/app/shared/ui/footer/footer.ts`
- `frontend/src/app/shared/ui/footer/footer.html`
- `frontend/src/app/shared/ui/footer/footer.css`

---

## 2. Navbar Fija (Sticky)

La barra de navegación ahora permanece fija en la parte superior al hacer scroll.

```css
header {
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
```

**Archivo modificado:** `frontend/src/app/shared/ui/header/header.css`

---

## 3. Sistema de Manejo de Errores

### Backend (Java)
Agregué bloques `try-catch` en `SentimentController.java`:
- Los errores ahora devuelven JSON con mensaje claro
- Se distingue entre errores de validación (400) y errores internos (500)

```java
catch (Exception e) {
    return ResponseEntity.internalServerError()
            .body(Map.of("error", "Error al analizar el comentario: " + e.getMessage()));
}
```

### Frontend (Angular)
Agregué visualización de errores en el componente de resultados:
- Si hay error, se muestra una tarjeta roja con icono de alerta
- Se ocultan los resultados/tabla mientras hay error
- Mensajes descriptivos según el tipo de error

**Mensajes de error implementados:**

| Código | Mensaje | Descripción |
|--------|---------|-------------|
| 0 | No se pudo conectar | Backend caído o sin internet |
| 400 | Solicitud inválida | Datos mal formados |
| 500 | Error interno | Problema en el servidor |
| 503 | Servicio no disponible | Servicio temporalmente caído |

**Archivos modificados:**
- `SentimentController.java` - try-catch en endpoints
- `inicio.ts` - señal `mensajeError` y lógica de captura
- `inicio.html` - paso de mensajeError al componente
- `resultados-analisis.ts` - input `mensajeError`
- `resultados-analisis.html` - visualización condicional del error
- `resultados-analisis.css` - estilos de la tarjeta de error

---

## 4. Validaciones Robustas

### Validación de espacios en blanco

**Frontend:**
- Validador personalizado `noSoloEspaciosValidator`
- Aplicado a textarea de comentario y input de nombre de columna
- Botón deshabilitado si el contenido es solo espacios

**Backend:**
- Validación extra para rechazar comentarios vacíos o solo espacios
- Mensaje: "El comentario no puede estar vacío o contener solo espacios."

### Validaciones para análisis masivo (CSV)

- **Nombre de columna**: No puede ser vacío o solo espacios
- **Columna inexistente**: Mensaje claro si no se encuentra
- **Comentarios vacíos en CSV**: Se filtran automáticamente antes de analizar
- **CSV sin datos válidos**: Mensaje claro si no hay comentarios para analizar

```java
// Filtrar comentarios vacíos del CSV
comentarios = comentarios.stream()
        .filter(c -> c != null && !c.trim().isEmpty())
        .map(String::trim)
        .toList();

if (comentarios.isEmpty()) {
    return ResponseEntity.badRequest()
            .body(Map.of("error", "No se encontraron comentarios válidos..."));
}
```

**Archivos modificados:**
- `inicio.ts` - validador en FormControl
- `inicio.html` - mensajes de error para soloEspacios
- `SentimentController.java` - validaciones del backend

---

## 5. Tabla de Errores Cubiertos

| Escenario | Frontend | Backend |
|-----------|----------|---------|
| Comentario vacío | ✅ Validators.required | ✅ Valida |
| Menos de 10 caracteres | ✅ Validators.minLength | - |
| Solo espacios (comentario) | ✅ noSoloEspaciosValidator | ✅ Valida |
| Solo espacios (columna) | ✅ noSoloEspaciosValidator | ✅ Valida |
| Backend caído | ✅ Mensaje descriptivo | - |
| Error interno (500) | ✅ Mensaje descriptivo | ✅ try-catch |
| Columna no existe en CSV | ✅ Muestra error | ✅ RuntimeException |
| CSV sin comentarios válidos | ✅ Muestra error | ✅ Valida |

---

## Cómo probar los errores

### Error de conexión (backend caído)
1. Detener el backend (Ctrl+C)
2. Intentar analizar un comentario en el frontend
3. Debería aparecer: "No se pudo conectar con el servidor..."

### Error de validación (solo espacios)
```powershell
Invoke-RestMethod -Uri "http://localhost:5000/sentiment-api/analizar-comentario" -Method Post -ContentType "application/json" -Body '{"text": "          "}'
```

---

## Notas Técnicas

- Los cambios del frontend se recargan automáticamente (hot reload)
- Los cambios del backend requieren reiniciar (`.\mvnw clean package -DskipTests` + `.\mvnw spring-boot:run`)
- La validación es de "defensa en profundidad": cliente Y servidor validan

---

# PARTE 3: Despliegue del Servidor (Deploy)

> **Fecha:** 9 de Enero de 2026
>
> Despliegue de la aplicación en la nube para evaluación del jurado.

---

## 1. Cambio de Estrategia: De Oracle OCI a Render

Inicialmente, el objetivo era desplegar en **Oracle Cloud Infrastructure (OCI)** para optar por el premio extra del hackathon. Sin embargo, surgieron inconvenientes insalvables con la validación de la tarjeta de crédito/débito en el registro de Oracle (no aceptaba tarjetas prepagas ni de terceros sin riesgo de bloqueo).

Para asegurar que el proyecto estuviera online y funcional para la evaluación, tomé la decisión ejecutiva de migrar la estrategia de despliegue a **Render.com**.

---

## 2. Dockerización del Backend

Dado que Render maneja servicios web de forma eficiente con contenedores, tuve que "dockerizar" nuestra aplicación Spring Boot.

**Acciones realizadas:**
- Creación del archivo `Dockerfile` en el directorio `app/`.
- Configuración de un build multi-stage para optimizar el tamaño de la imagen:
  1. **Stage Build:** Usa Maven y JDK 21 para compilar el proyecto y generar el `.jar`.
  2. **Stage Run:** Usa una imagen ligera de JDK 21 Alpine para ejecutar la aplicación.

```dockerfile
# Resumen del Dockerfile creado
FROM maven:3.9.6-eclipse-temurin-21 AS build
# ... copiado de archivos y compilación ...
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## 3. Configuración del Frontend para Producción

Para que el Frontend (Angular) funcionara en la nube y se conectara al Backend desplegado:

1.  **Variables de Entorno:**
    - Se crearon los archivos `src/environments/environment.ts` (local) y `src/environments/environment.prod.ts` (producción).
    - En producción, la URL de la API apunta al servicio de Render (`https://sentiment-api-backend.onrender.com`).

2.  **Angular Build:**
    - Se configuró `angular.json` para reemplazar el archivo de entorno automáticamente al ejecutar `npm run build -- --configuration=production`.

3.  **Servicio de Angular:**
    - Se refactorizó `SentimentApiService` para usar la variable `environment.apiUrl` en lugar de la URL hardcodeada a localhost.

---

## 4. Estado Final del Despliegue

La aplicación quedó desplegada en dos servicios vinculados en Render:

1.  **Backend (Web Service):** Ejecutando Spring Boot via Docker.
2.  **Frontend (Static Site):** Sirviendo los archivos compilados de Angular.

**Resultado:** El proyecto es accesible públicamente, cumpliendo con el requisito fundamental de disponibilidad para los jurados.

---

## 5. Solución de Problemas Técnicos (Troubleshooting)

Durante el proceso de despliegue surgieron tres desafíos críticos que fueron resueltos para asegurar la estabilidad:

### A. Error de "Pantalla Blanca" (Missing Index)
- **Problema:** La configuración inicial de Angular (`angular.json`) no incluía la propiedad `"index"`, por lo que el proceso de compilación generaba los scripts JS pero no el archivo `index.html` principal.
- **Solución:** Se corrigió `angular.json` agregando `"index": "src/index.html"` en la configuración de build, permitiendo que Render sirviera la aplicación correctamente.

### B. Bloqueo de Conexión (CORS)
- **Problema:** El navegador bloqueaba las peticiones del Frontend hacia el Backend debido a que residen en dominios diferentes en Render.
- **Solución:** Se implementó la configuración de **Cross-Origin Resource Sharing (CORS)** en el Backend Spring Boot, añadiendo la anotación `@CrossOrigin("*")` en `SentimentController.java`.

### C. Error de Enrutamiento al Recargar (Hash Strategy)
- **Problema:** Al ser una Single Page Application (SPA), la navegación profunda (ej. `/inicio`) funcionaba virtualmente, pero al recargar la página (F5), el servidor devolvía un error 404 porque buscaba un archivo físico inexistente.
- **Solución:** Se migró la estrategia de enrutamiento de Angular a `HashLocationStrategy`.
    - Las URLs ahora tienen el formato `/#/inicio`.
    - Esto garantiza que el servidor siempre reciba peticiones a la raíz (`/`), delegando el enrutamiento interno al cliente y eliminando los errores de "Not Found" en la nube.
