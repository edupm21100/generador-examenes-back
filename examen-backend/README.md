# 🚀 API REST - Gestión de Usuarios y Roles (Examen Backend)

Este proyecto es una API REST profesional desarrollada en **Spring Boot** para la gestión integral de un sistema de usuarios y sus roles asociados. Implementa una arquitectura por capas, manejo avanzado de relaciones en base de datos, documentación interactiva y una sólida cobertura de pruebas unitarias.

---

## 🛠️ Tecnologías Utilizadas

* **Java** (JDK 21)
* **Spring Boot** 4.0.3 (Web, Data JPA, Validation)
* **Base de Datos:** MySQL
* **Mapeo de Objetos:** ModelMapper
* **Documentación:** OpenAPI 3 / Swagger UI y Javadoc
* **Testing:** JUnit 5 y Mockito

---

## ⚙️ Características Principales

* **Arquitectura Multicapa:** Separación clara entre `Controllers`, `Services`, `Repositories` y `DTOs`.
* **Relación Many-To-Many:** Gestión compleja entre Usuarios y Roles. Incluye desvinculación automática en cascada al eliminar registros para evitar errores de integridad (`TransientPropertyValueException`).
* **Reglas de Negocio Estrictas:**
  * Cambio de contraseñas requiriendo la clave actual.
  * Asignación y revocación de roles validando permisos de Administrador.
* **Borrado Dual:** Soporte tanto para borrado físico (permanente) como borrado lógico (desactivación de cuentas).
* **Manejo Global de Excepciones:** Uso de `@RestControllerAdvice` para capturar errores personalizados (`NotFoundException`, `BadRequestException`) y devolver respuestas JSON limpias y estructuradas.
* **Vistas JSON Dinámicas:** Uso de `@JsonView` para ocultar datos sensibles (como contraseñas) en las respuestas HTTP dependiendo del endpoint.

---

## 📖 Documentación de la API

El proyecto incluye documentación interactiva donde puedes probar todos los endpoints sin necesidad de Postman.

1. Arranca la aplicación.
2. Abre tu navegador y navega a:  
   👉 `http://localhost:8080/swagger-ui.html`
