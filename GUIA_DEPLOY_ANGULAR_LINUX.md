# 🚀 Guía de Deploy: Angular en Linux

> **Preparado por:** Equipo 31 - Hackathon Latinoamérica
> 
> **Fecha:** Enero 2026

---

## Tabla de Contenidos

1. [Opción 1: Nginx (Recomendada)](#opción-1-nginx---recomendada)
2. [Opción 2: Docker](#opción-2-docker)
3. [Opción 3: Hosting Gratuito](#opción-3-hosting-gratuito)
4. [Consideraciones Importantes](#consideraciones-importantes)

---

## Opción 1: Nginx - Recomendada

### Paso 1: Generar el build de producción

```bash
cd frontend
npm run build
# Esto genera: dist/frontend/browser/
```

### Paso 2: Instalar Nginx en el servidor

```bash
sudo apt update
sudo apt install nginx -y
```

### Paso 3: Copiar archivos al servidor

```bash
# Desde tu máquina local:
scp -r dist/frontend/browser/* usuario@IP_SERVIDOR:/var/www/html/

# O en el servidor directamente:
sudo cp -r dist/frontend/browser/* /var/www/html/
```

### Paso 4: Configurar Nginx

Editar `/etc/nginx/sites-available/default`:

```nginx
server {
    listen 80;
    server_name tu-dominio.com;
    
    root /var/www/html;
    index index.html;
    
    # IMPORTANTE: Esto permite que las rutas de Angular funcionen
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # Cacheo de assets estáticos
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### Paso 5: Reiniciar Nginx

```bash
sudo nginx -t              # Verificar configuración
sudo systemctl restart nginx
sudo systemctl enable nginx  # Iniciar con el sistema
```

---

## Opción 2: Docker

### Archivo `Dockerfile`

Crear en la raíz del proyecto frontend:

```dockerfile
# Etapa 1: Build de Angular
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Etapa 2: Servidor Nginx
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Archivo `nginx.conf`

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    server {
        listen 80;
        root /usr/share/nginx/html;
        index index.html;
        
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        gzip on;
        gzip_types text/plain text/css application/json application/javascript;
    }
}
```

### Comandos Docker

```bash
# Construir imagen
docker build -t sentimentapi-frontend .

# Ejecutar contenedor
docker run -d -p 80:80 --name frontend sentimentapi-frontend

# Ver logs
docker logs frontend

# Detener
docker stop frontend
```

---

## Opción 3: Hosting Gratuito

### Vercel (Más fácil)

```bash
npm install -g vercel
cd frontend
vercel
# Seguir las instrucciones interactivas
```

### GitHub Pages

```bash
npm install -g angular-cli-ghpages
ng build --base-href=/nombre-repo/
npx angular-cli-ghpages --dir=dist/frontend/browser
```

### Firebase Hosting

```bash
npm install -g firebase-tools
firebase login
firebase init hosting
# Seleccionar: dist/frontend/browser
firebase deploy
```

### Netlify

1. Ir a [netlify.com](https://netlify.com)
2. Arrastrar la carpeta `dist/frontend/browser`
3. ¡Listo!

---

## Consideraciones Importantes

### ⚠️ Rutas de Angular (SPA)

**Problema:** Al refrescar una ruta como `/analizar`, el servidor busca un archivo `/analizar/index.html` que no existe.

**Solución:** Siempre usar `try_files $uri $uri/ /index.html;` en la configuración de Nginx.

### 🔐 HTTPS con Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d tu-dominio.com
# Renovación automática
sudo certbot renew --dry-run
```

### 🌐 Variables de Entorno

Editar `src/environments/environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.tu-dominio.com'  // URL del backend en producción
};
```

### 📊 Monitoreo

```bash
# Ver estado de Nginx
sudo systemctl status nginx

# Ver logs de acceso
sudo tail -f /var/log/nginx/access.log

# Ver logs de errores
sudo tail -f /var/log/nginx/error.log
```

---

## Resumen de Comandos

| Acción | Comando |
|--------|---------|
| Build producción | `npm run build` |
| Copiar al servidor | `scp -r dist/* usuario@IP:/var/www/html/` |
| Reiniciar Nginx | `sudo systemctl restart nginx` |
| Ver logs | `sudo tail -f /var/log/nginx/error.log` |
| SSL gratis | `sudo certbot --nginx -d dominio.com` |

---

## Estructura de Archivos Generados

```
dist/
└── frontend/
    └── browser/
        ├── index.html          # Punto de entrada
        ├── main-XXXXX.js       # Código Angular
        ├── polyfills-XXXXX.js  # Compatibilidad
        ├── styles-XXXXX.css    # Estilos
        └── assets/             # Imágenes, fuentes, etc.
```

---

