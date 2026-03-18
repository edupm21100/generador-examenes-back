# 🎓 Generador de Exámenes - Arquitectura Backend & CI/CD 🚀

Este repositorio contiene la arquitectura backend para un sistema de generación de exámenes, diseñado con un enfoque de microservicios (enrutamiento inteligente) y un ciclo de vida de desarrollo completamente automatizado (CI/CD).

## 🏗️ Arquitectura del Sistema

El proyecto utiliza un patrón de **Despliegue de Monolito Basado en Enrutamiento**, logrando resiliencia y aislamiento de tráfico sin duplicar bases de código:

* **API Gateway (Puerto 8082):** El punto de entrada único. Enruta el tráfico dinámicamente según la URL (`/incidencias/**` vs el resto).
* **Eureka Server (Puerto 8761):** Servidor de descubrimiento. Registra dinámicamente las instancias de los servicios.
* **Backend Principal (Aislado en Red Interna):** Procesa el tráfico general (Usuarios, Auth, etc.).
* **Backend Incidencias (Aislado en Red Interna):** Instancia clonada dedicada exclusivamente a procesar las incidencias, garantizando tolerancia a fallos.
* **Base de Datos:** MySQL (desplegada en contenedor).

*Nota de Seguridad:* Los backends no exponen puertos al host (`localhost`), obligando a que todo el tráfico pase por el API Gateway y su validación.

## ⚙️ Stack Tecnológico

* **Lenguaje & Framework:** Java 21, Spring Boot 4.0.3
* **Construcción:** Maven
* **Seguridad:** Spring Security, JWT
* **Contenedores:** Docker & Docker Compose (Imágenes base Alpine/Distroless para máxima seguridad).

## 🏭 Pipeline CI/CD (Automatización Total)

El proyecto cuenta con un pipeline de Integración y Despliegue Continuo gestionado por **Jenkins** y auditado por **SonarQube**.

1.  **Disparador (SCM Polling):** Jenkins detecta automáticamente los `git push` en la rama `main`.
2.  **Build:** Compilación limpia usando Maven Wrapper (`mvn clean package -DskipTests`).
3.  **Code Quality (SonarQube):** Análisis estático de código, vulnerabilidades y code smells.
4.  **Quality Gate:** El pipeline se detiene automáticamente si SonarQube no aprueba la calidad del código.
5.  **Deploy (Docker in Docker):** Jenkins instala el cliente de Docker al vuelo, derriba los contenedores antiguos y levanta la nueva versión sin intervención manual (`docker compose up -d --build`).

## 🚀 Cómo ejecutarlo localmente (Modo Desarrollo)

Si deseas levantar el entorno de desarrollo sin el pipeline de Jenkins, asegúrate de tener Docker instalado y ejecuta en la raíz del proyecto:

```bash
# Levantar toda la infraestructura
docker compose up -d --build
