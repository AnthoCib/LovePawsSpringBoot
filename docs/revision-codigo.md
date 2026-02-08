# Revisión general de código — LovePawsSpringBoot

Fecha: 2026-02-08

## Alcance revisado
- Estructura backend Spring Boot (controladores, servicios, repositorios, seguridad, storage y mail).
- Configuración principal (`application.properties`, `pom.xml`) y pruebas.
- Plantilla general de frontend (Thymeleaf/CSS) a nivel de estructura, no QA visual exhaustivo.

## Qué falta completar (prioridad alta)

1. **Pruebas automatizadas reales (unitarias/integración)**
   - Actualmente solo existe una prueba de arranque de contexto.
   - Falta cobertura de flujos críticos: registro/login, creación de mascota, flujo de solicitud/adopción, reportes y permisos por rol.

2. **Implementar filtros por rango de fecha en reportes**
   - La interfaz de reportes define métodos con `desde/hasta`, pero la implementación actual ignora esos parámetros y devuelve consultas globales.
   - Falta ajustar consultas JPA y consumo en controlador/vista para que el filtro funcione de verdad.

3. **Gestión segura de secretos y credenciales**
   - Hay credenciales sensibles en `application.properties` (BD y correo) y configuración placeholder de S3.
   - Falta mover secretos a variables de entorno / vault y dejar valores seguros por perfil.

4. **Fortalecer validaciones y manejo de errores funcionales**
   - Hay múltiples `RuntimeException` genéricas para validaciones de negocio.
   - Falta estandarizar excepciones de dominio + mensajes de usuario + códigos HTTP/redirect consistentes.

5. **Completar estrategia de logging (quitar prints de consola)**
   - Hay `System.out.println` de depuración en el flujo de registro.
   - Falta reemplazar por logger estructurado (`Slf4j`) con niveles (`debug/info/warn/error`).

## Qué falta completar (prioridad media)

6. **Endurecimiento de carga de archivos (upload)**
   - El servicio valida MIME y tamaño, pero faltan medidas adicionales: lista blanca estricta de extensiones, validación de contenido real, normalización de nombre original y escaneo si aplica.

7. **Runner de correo de prueba para entorno productivo**
   - Existe un `CommandLineRunner` de prueba de email con datos hardcodeados (aunque el envío está comentado).
   - Falta encapsularlo en perfil `dev` o retirarlo del build principal para evitar ruido/riesgo.

8. **Robustez en generación de PDF (logo/rutas)**
   - El PDF intenta cargar un logo desde una ruta que no coincide con los recursos existentes, por lo que cae silenciosamente en fallback.
   - Falta resolver carga por classpath para portabilidad.

9. **Homologar y limpiar deuda técnica marcada como TODO**
   - Varias clases tienen comentarios `TODO Auto-generated method stub` aunque sí implementan lógica.
   - Falta limpiar esos TODO para distinguir deuda real de ruido.

10. **Estándar de configuración por ambiente (dev/stage/prod)**
   - Falta separar `application-dev.properties` y `application-prod.properties` con políticas claras (SQL logs, credenciales, mail, uploads).

## Qué está razonablemente bien encaminado
- Arquitectura por capas (controller/service/repository) consistente.
- Seguridad con Spring Security y redirección por roles.
- Soft delete de usuario (`deletedAt`) ya planteado.
- Restricciones de negocio importantes en adopción (ej. no duplicar solicitudes pendientes) implementadas.

## Siguiente plan recomendado (orden de ejecución)
1. Base de calidad: pruebas + perfiles de configuración + secretos.
2. Reportes: filtros por fecha y validación con tests.
3. Seguridad/operación: logging estructurado y endurecimiento de upload.
4. Limpieza técnica: TODOs, runner de pruebas y rutas de recursos PDF.

## Comandos ejecutados en la revisión
- `rg --files`
- `rg -n "TODO|FIXME|PENDIENTE|throw new UnsupportedOperationException|return null;|System.out.println\(" src/main/java src/main/resources/templates pom.xml`
- `bash ./mvnw test -q` *(falló por descarga de Maven wrapper en el entorno)*
