<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:7aa2f7,50:bb9af7,100:1a1b26&height=200&section=header&text=GFC%20-%20GLUD%20Fried%20Chicken&fontSize=40&fontColor=c0caf5&animation=fadeIn&fontAlignY=35&desc=Diseno%20de%20Software%20-%20Taller%20Metricas&descSize=16&descAlignY=55&descAlign=50" width="100%">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Astro-7.3-FF5D01?style=for-the-badge&logo=astro&logoColor=white" alt="Astro">
  <img src="https://img.shields.io/badge/Docker-24-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/H2-2.2-000000?style=for-the-badge&logo=databricks&logoColor=white" alt="H2">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Lombok-1.18-CC0000?style=for-the-badge&logo=dataclassesdotjava&logoColor=white" alt="Lombok">
  <img src="https://img.shields.io/badge/Swagger-2.6-85EA2D?style=for-the-badge&logo=openapiinitiative&logoColor=white" alt="Swagger">
  <img src="https://img.shields.io/badge/pnpm-9-CC0000?style=for-the-badge&logo=pnpm&logoColor=white" alt="pnpm">
  <img src="https://img.shields.io/badge/Tailwind%20CSS-v4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white" alt="Tailwind">
</p>

---

## Sobre el proyecto

**GLUD Fried Chicken (GFC)** es una aplicacion CRUD para la gestion de una empresa especializada en la preparacion de comidas rapidas. Desarrollada como parte del **Taller de Metricas de Software** de la asignatura de Diseno de Software.

La aplicacion permite gestionar:
- **Productos** del menu (combos, hamburguesas, pollos, papas, bebidas, postres)
- **Clientes** (registro, actualizacion, eliminacion)

---

## Integrantes

| Nombre | Codigo |
|--------|--------|
| Daniel Mateo Ballesteros Molina | 20221020102 |
| Jan Henrik Sanchez Jerez | 20231020130 |
| Juan Carlos Quintero Rubiano | 20232020172 |
| Juan Diego Peralta Barbosa | 20231020146 |

---

## Stack tecnologico

| Capa | Tecnologia | Puerto |
|------|-----------|--------|
| Backend | Java 21 + Spring Boot 3.3 | 8080 |
| Frontend | Astro 7.3 + Tailwind v4 | 4321 |
| Base de datos (dev) | H2 In-Memory | - |
| Base de datos (prod) | PostgreSQL 16 | 5432 |
| Container | Docker + Docker Compose | - |

---

## Estructura del proyecto

```
dise-o_taller_metricas_software/
  backend/           # Spring Boot - API REST
    src/
      main/java/com/gfc/
      main/resources/
    pom.xml
    Dockerfile
  frontend/          # Astro - Interfaz web
    src/
    astro.config.mjs
    package.json
    Dockerfile
  docs/              # Documento de resultados
  scripts/           # Scripts de automatizacion
  docker-compose.yml
  .github/workflows/ # CI - Source Monitor + Kivati
```

---

## Ejecucion local

**Requisitos:** Java 21+, Node.js 20+, Docker (opcional)

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
corepack pnpm install
corepack pnpm dev
```

**Con Docker Compose:**
```bash
docker compose up --build
```

- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Frontend: http://localhost:4321
- H2 Console: http://localhost:8080/h2-console

---

## Diagrama Kivati - Metricas SourceMonitor

Cada tag genera automaticamente un diagrama Kivati con las metricas del codigo fuente.

**Metricas evaluadas:**

| Metrica | Rango aceptable | Descripcion |
|---------|----------------|-------------|
| Lineas de Codigo (M0) | < 5000 | Tamaño total del proyecto |
| Sentencias (M1) | < 2000 | Sentencias ejecutables |
| Metodos por Clase (M6) | 3 - 8 | Complejidad de clases |
| Sentencias por Metodo (M7) | 3 - 8 | Tamaño de metodos |
| Complejidad Ciclomatica Max (M10) | 1 - 10 | Riesgo de errores |
| Profundidad Promedio de Bloques (M13) | 1 - 2 | Anidamiento del codigo |

```bash
git tag -a v1.0.0 -m "Version 1.0"
git push origin v1.0.0
```

---

## SonarQube - Analisis de calidad

Cada tag tambien lanza un analisis de calidad con SonarCloud. Este analisis
**solo reporta resultados, no bloquea nada**: aunque el Quality Gate no pase,
el pipeline termina en exito.

**Metricas de SonarQube (ISO 25010):**

| Metrica | Descripcion |
|---------|-------------|
| Maintainability Rating (A-E) | Mantenibilidad del codigo |
| Reliability Rating (A-E) | Fiabilidad (bugs) |
| Security Rating (A-E) | Seguridad (vulnerabilidades) |
| Complejidad Ciclomatica | Complejidad por metodo |
| Cobertura (%) | % de codigo cubierto por tests |
| Lineas duplicadas (%) | Duplicacion de codigo |
| Deuda tecnica (horas) | Tiempo estimado para arreglar issues |
| Code Smells | Malos olores de codigo |

**Configuracion previa (una sola vez):**

1. Crea una cuenta en [SonarCloud](https://sonarcloud.io) y un proyecto analizado
   desde GitHub.
2. Configura estos secrets/variables en el repo (Settings > Secrets and variables):
   - `SONAR_TOKEN` (secreto): token de SonarCloud
   - `SONAR_PROJECT_KEY` (variable): project key de tu proyecto
   - `SONAR_ORGANIZATION` (variable): organizacion de SonarCloud

---

## Entregables

1. Codigo fuente de la aplicacion (Java + Astro)
2. Diagrama de clases de la aplicacion
3. Diagrama de casos de uso de la aplicacion
4. Documento con resultados Source Monitor + SonarQube e interpretacion (en `docs/`)

**Fecha de entrega:** 8 de septiembre, 16:00

---

> **Nota:** Cada integrante puede crear ramas `feature/*` o `fix/*` y hacer merge via PR a `main`.
