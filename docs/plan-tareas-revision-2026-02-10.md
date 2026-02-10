# Plan de tareas propuesto tras revisión de código

Fecha: 2026-02-10

## 1) Tarea de corrección tipográfica

**Título sugerido:** Estandarizar nombre de stylesheet `boostrap.css` a `bootstrap.css`.

**Problema detectado:**
- La hoja de estilos local para login se llama `boostrap.css` y es referenciada con ese mismo nombre.
- Aunque funciona técnicamente, el nombre contiene un error tipográfico que puede generar confusión en mantenimiento, búsquedas y onboarding.

**Alcance recomendado:**
- Renombrar `src/main/resources/static/css/boostrap.css` a `bootstrap.css`.
- Actualizar referencias en `src/main/resources/templates/login.html` y `src/main/resources/templates/usuario/login.html`.

**Criterio de aceptación:**
- Ninguna referencia en el repo a `boostrap.css`.
- Login principal y login de usuario cargan estilos sin errores 404.

---

## 2) Tarea para solucionar un fallo funcional

**Título sugerido:** Devolver 403 (no 500) cuando un usuario consulta adopciones de otro usuario en API.

**Problema detectado:**
- En `AdopcionApiController`, el endpoint `GET /api/adopciones/usuario/{usuarioId}` lanza `IllegalArgumentException` cuando el usuario no autorizado intenta consultar otro historial.
- Sin manejo explícito, este patrón suele terminar en 500, cuando semánticamente corresponde 403 Forbidden.

**Alcance recomendado:**
- Reemplazar `IllegalArgumentException` por `AccessDeniedException` o `ResponseStatusException(HttpStatus.FORBIDDEN, ...)`.
- (Opcional recomendado) Añadir `@ControllerAdvice` para mapear excepciones de dominio a códigos HTTP consistentes.

**Criterio de aceptación:**
- Un adoptante autenticado consultando `/api/adopciones/usuario/{id-distinto}` recibe 403.
- Un admin autenticado sigue pudiendo consultar cualquier `usuarioId`.

---

## 3) Tarea para comentario/documentación (discrepancia)

**Título sugerido:** Corregir hallazgos desactualizados en `docs/revision-requerimientos.md`.

**Problema detectado:**
- El documento reporta como faltantes varias vistas (por ejemplo `mascota/detalle` y `adopcion/mis-adopciones`) que actualmente sí existen en el árbol de templates.
- Esto puede llevar a priorizar trabajo ya realizado y distorsiona el estado real del proyecto.

**Alcance recomendado:**
- Actualizar la sección “Evidencia técnica verificada nuevamente” eliminando falsos positivos.
- Mantener solo brechas reproducibles con comando y fecha.

**Criterio de aceptación:**
- Cada vista marcada como faltante en el documento debe estar realmente ausente o, en caso contrario, retirarse del listado.
- Agregar fecha de verificación y comandos usados.

---

## 4) Tarea para mejorar una prueba

**Título sugerido:** Añadir prueba de seguridad para autorización en `GET /api/adopciones/usuario/{usuarioId}`.

**Problema detectado:**
- No hay cobertura de prueba para la regla de autorización de historial por usuario/rol en la API de adopciones.
- El comportamiento (403 vs 200) es crítico de seguridad y actualmente no está blindado por tests automáticos.

**Alcance recomendado:**
- Crear test con `@WebMvcTest(AdopcionApiController.class)` + `spring-security-test` o integración con `MockMvc`.
- Cubrir al menos 3 casos:
  1. Adoptante consulta su propio `usuarioId` -> 200.
  2. Adoptante consulta `usuarioId` de otro usuario -> 403.
  3. Admin consulta cualquier `usuarioId` -> 200.

**Criterio de aceptación:**
- La suite falla si se rompe el control de acceso.
- El test se ejecuta en CI sin depender de servicios externos.
