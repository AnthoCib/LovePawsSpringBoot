# Verificación de compatibilidad: proyecto LovePaws vs `bd_love_paws_final`

## Resultado ejecutivo

**Compatibilidad general: ALTA con ajustes aplicados.**

Se revisó el mapeo JPA contra el DDL propuesto y se detectaron diferencias críticas que ya se corrigieron en esta iteración:

1. `EstadoAdopcion` apuntaba a tabla incorrecta (`estado_mascota`) y se ajustó a `estado_adopcion`.
2. `SeguimientoPostAdopcion` usaba columna `estado_mascota` tipo texto y se normalizó a FK `estado_id` hacia `estado_mascota`.
3. Se agregó entidad/repo para `respuesta_seguimiento_adoptante` (tabla existente en tu diseño, no modelada antes).
4. Se reflejó en entidad la unicidad `(id_usuario, mascota_id)` de `solicitud_adopcion`.
5. Se actualizó datasource para usar `bd_love_paws_final`.

## Matriz de concordancia por tabla

| Tabla SQL | Estado | Notas |
|---|---|---|
| `estado_usuario` | ✅ | Mapeada por `EstadoUsuario`. |
| `estado_mascota` | ✅ | Mapeada por `EstadoMascota`. |
| `estado_adopcion` | ✅ | Corregido mapeo de `EstadoAdopcion`. |
| `rol` | ✅ | Mapeada por `Rol`. |
| `usuario` | ✅ | Mapeada por `Usuario` con soft delete. |
| `especie` | ✅ | Mapeada por `Especie`. |
| `raza` | ✅ | Mapeada por `Raza`. |
| `categoria` | ✅ | Mapeada por `Categoria`. |
| `mascota` | ✅ | Mapeada por `Mascota`, estado como FK. |
| `solicitud_adopcion` | ✅ | Mapeada por `SolicitudAdopcion`, se añadió unique constraint en entidad. |
| `adopcion` | ✅ | Mapeada por `Adopcion` con referencia a solicitud. |
| `seguimiento_post_adopcion` | ✅ | Corregido para usar `estado_id` como FK. |
| `respuesta_seguimiento_adoptante` | ✅ | Se añadió entidad y repositorio. |
| `auditoria` | ✅ | Mapeada por `Auditoria`. |

## Observaciones recomendadas (no bloqueantes)

- Mantener `spring.jpa.hibernate.ddl-auto=none` como está, para respetar tu esquema normalizado y evitar drift por Hibernate.
- Si vas a ejecutar seeds SQL con IDs específicos (ej. admin `id=1000`), confirma que no exista conflicto con datos previos.
- Considerar migraciones versionadas (Flyway/Liquibase) para aplicar esta BD en dev/test/prod de forma consistente.
