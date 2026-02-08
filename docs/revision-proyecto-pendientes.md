# Revisión técnica: qué falta para completar LovePaws

Fecha: 2026-02-08

## Diagnóstico ejecutivo

El proyecto tiene una base funcional sólida (módulos de usuario, mascotas, adopción, gestor/admin), pero aún no está listo para considerarse “completo” ni para operación de producción. Los faltantes principales se agrupan en **build/deploy**, **seguridad**, **pruebas automatizadas**, y **operación**.

## Faltantes críticos (bloqueantes)

1. **Build no reproducible en entorno limpio**
   - El `parent` de Spring Boot está fijado a `3.5.10`, versión que no se pudo resolver desde Maven Central en la verificación local.
   - Impacto: no se puede compilar/ejecutar CI en un entorno estándar.

2. **Credenciales sensibles en repositorio**
   - `application.properties` contiene usuario y password SMTP en texto plano.
   - Impacto: riesgo de seguridad inmediato, exposición de secretos y posible abuso de cuenta de correo.

3. **Estrategia de configuración no separada por entorno**
   - Solo existe un `application.properties` con parámetros locales (DB, mail, `spring.profiles.active=dev`).
   - Impacto: despliegue inseguro/frágil en QA/producción.

## Faltantes funcionales de cierre

4. **Cobertura de pruebas insuficiente**
   - Actualmente solo existe una prueba de carga de contexto (`contextLoads`).
   - Faltan pruebas unitarias y de integración para:
     - flujo de solicitud de adopción;
     - aprobación/rechazo;
     - validaciones de perfil y seguridad por roles;
     - filtros del catálogo de mascotas.

5. **Métricas/KPIs de proceso incompletos**
   - Hay dashboard y reportes básicos, pero falta consolidar métricas de ciclo completo (ej. tiempos por etapa, backlog por gestor, tasas de conversión por estado).

6. **Documentación operativa incompleta**
   - Falta guía única de arranque y operación (variables requeridas, seeds, credenciales por entorno, estrategia de despliegue, recuperación ante fallos).

## Faltantes de calidad para “proyecto terminado”

7. **Pipeline CI/CD**
   - No hay evidencia en repo de workflow automatizado (build, tests, análisis estático, empaquetado).

8. **Migraciones versionadas de base de datos**
   - Se usa `ddl-auto=none` (correcto para evitar drift), pero faltan scripts de migración gestionados (Flyway/Liquibase) para trazabilidad entre entornos.

9. **Hardening adicional de seguridad**
   - Reforzar rate limiting / lockout en login, políticas de contraseñas, y revisión de cabeceras de seguridad y auditoría de eventos sensibles.

## Orden recomendado para cerrar proyecto

### Sprint 1 (bloqueante técnico)
- Ajustar versión de Spring Boot parent a una liberada y validada por CI.
- Externalizar secretos a variables de entorno/secret manager.
- Crear `application-dev`, `application-test`, `application-prod`.

### Sprint 2 (calidad y funcional)
- Implementar batería mínima de pruebas (unit + integración) para flujo de adopción y seguridad.
- Añadir pipeline CI (build + test + quality gate).
- Definir migraciones SQL versionadas.

### Sprint 3 (cierre operacional)
- Completar KPIs de proceso y reportes ejecutivos.
- Documentación final de operación y despliegue.
- Checklist de readiness de producción.
