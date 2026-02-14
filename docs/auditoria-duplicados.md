# Auditoría técnica de duplicados (Spring Boot + Maven + STS/Eclipse)
Este informe fue generado con `scripts/audit_duplicates.py` y validado manualmente.
## Resumen ejecutivo
- Duplicados por **nombre**: 6
- Duplicados por **contenido (SHA-256)**: 18 grupos
- Duplicados de **simpleName Java**: 0
- Duplicados de **nombre de template Thymeleaf**: 6
- Duplicados por nombre en **static/**: 0
- Duplicados por nombre en **tests**: 0
- Colisiones de **bean name**: 0
- Colisiones de **mapping (HTTP+ruta)**: 0
- `.classpath` presente en repo: No
## 1) Duplicados por nombre de archivo
### `dashboard.html`
- `src/main/resources/templates/admin/dashboard.html`
- `src/main/resources/templates/admin/reportes/dashboard.html`
- `src/main/resources/templates/gestor/dashboard.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.
### `form.html`
- `src/main/resources/templates/gestor/mascota/form.html`
- `src/main/resources/templates/gestor/mascotas/form.html`
- `src/main/resources/templates/mascota/form.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.
### `lista.html`
- `src/main/resources/templates/gestor/mascota/lista.html`
- `src/main/resources/templates/mascota/lista.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.
### `login.html`
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/usuario/login.html`
- `src/main/resources/templates/usuarios/login.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.
### `mascotas.html`
- `src/main/resources/templates/admin/mascotas.html`
- `src/main/resources/templates/admin/reportes/mascotas.html`
- `src/main/resources/templates/gestor/mascotas.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.
### `usuarios.html`
- `src/main/resources/templates/admin/reportes/usuarios.html`
- `src/main/resources/templates/admin/usuarios.html`
- Impacto: ambigüedad en mantenimiento y posible referencia incorrecta.
- Recomendación: unificar, renombrar por contexto funcional y actualizar referencias.

## 2) Duplicados por contenido (copias exactas)
### Grupo 1 (`132078d24eb2…`)
- `src/main/resources/static/images/mascotas/cc35.jpg`
- `uploads/32a82994-728c-469a-b968-5eb51e7f743f.jpg`
- `uploads/d843801e-800f-4564-9013-111d82acfcfe.jpg`
### Grupo 2 (`1515db6248a1…`)
- `src/main/resources/static/images/mascotas/cc22.jpg`
- `uploads/7b456f74-2aff-491c-b255-2e81c96c86aa.jpg`
### Grupo 3 (`175a81e212e3…`)
- `src/main/resources/static/images/mascotas/cc20.jpg`
- `uploads/f73376a7-57c6-4435-8e29-eaa8c49a09d5.jpg`
### Grupo 4 (`27236b741dd6…`)
- `src/main/resources/static/images/mascotas/pp10.jpg`
- `src/main/resources/static/uploads/f06757b1-cf0d-4103-ab17-270c29c043c3.jpg`
### Grupo 5 (`36119b03f65e…`)
- `src/main/resources/static/images/mascotas/cc21.jpg`
- `uploads/c5978b74-0516-498d-958e-4e5a969b5e06.jpg`
### Grupo 6 (`4240930a05f4…`)
- `src/main/resources/static/uploads/7fe5f689-739a-4789-995f-44656046297b.jpg`
- `uploads/8fa6240d-f132-4d95-8b7c-143251229e79.jpg`
- `uploads/e34b9c3f-f38d-4432-90d6-f49272cca253.jpg`
### Grupo 7 (`52d4f285eac3…`)
- `src/main/resources/static/images/mascotas/dp31.jpg`
- `src/main/resources/static/uploads/21f2586e-1b62-4a39-a731-8bd6f1491ed5.jpg`
### Grupo 8 (`6227fc14c714…`)
- `src/main/resources/static/images/mascotas/daisy.jpg`
- `src/main/resources/static/uploads/03177191-f505-4002-94b4-245236634802.jpg`
- `src/main/resources/static/uploads/e3b32964-be83-4ad5-b663-3689d5fffb4f.jpg`
### Grupo 9 (`698855131c83…`)
- `src/main/resources/static/images/mascotas/cc39.jpg`
- `uploads/e08947ef-32b9-4a8b-a150-7520c1a55d98.jpg`
### Grupo 10 (`780c32fe5002…`)
- `src/main/resources/static/images/mascotas/pp01.jpg`
- `src/main/resources/static/uploads/e0668366-5f36-4a60-9887-39b0d430161a.jpg`
### Grupo 11 (`7c0aaaec2fbf…`)
- `src/main/resources/static/images/mascotas/cc13.jpg`
- `uploads/a1b962d5-b6cf-4703-b46c-682ca561295d.jpg`
- `uploads/a591cbad-e1e6-4a24-bbe7-17097604ee21.jpg`
### Grupo 12 (`7e64ae12de29…`)
- `src/main/resources/static/images/mascotas/pp07.jpg`
- `uploads/cf07d320-adaa-4b0e-ade1-b3605603f1d4.jpg`
### Grupo 13 (`8333faa8ee0e…`)
- `src/main/resources/static/images/mascotas/cc03.jpg`
- `uploads/220b1bea-4150-4152-a72b-2468e362e424.jpg`
### Grupo 14 (`9c9874bfeee7…`)
- `src/main/resources/static/uploads/0e099481-3439-4bc9-8671-bde03d532c80.jpg`
- `uploads/983d701c-553c-47a6-a524-932dd5869418.jpg`
### Grupo 15 (`b1e3a980c1cb…`)
- `src/main/resources/static/images/mascotas/bruno.jpg`
- `src/main/resources/static/images/mascotas/pp09.jpg`
### Grupo 16 (`ce8330ee3653…`)
- `src/main/resources/static/images/mascotas/cc07.jpg`
- `src/main/resources/static/uploads/57897043-390d-4bc7-be90-5f79297cbf34.jpg`
- `uploads/8b32709c-5ef1-40ef-bfe9-e258f4a4fd19.jpg`
### Grupo 17 (`e82cbbbad1d4…`)
- `src/main/resources/static/images/mascotas/cc01.jpg`
- `uploads/89ce3ab2-8a5b-4b2d-98f0-01d855f0e38d.jpg`
- `uploads/8fe21921-a77d-4d62-8451-07e5796bbc8a.jpg`
### Grupo 18 (`f5e804dc6c2a…`)
- `src/main/resources/static/images/mascotas/cc40.jpg`
- `uploads/ee6a1d36-974c-4533-aa96-f79d170993a6.jpg`
- Impacto: tamaño de repo/artefacto mayor, riesgo de inconsistencias de referencia.
- Recomendación: conservar un archivo canónico por grupo y actualizar referencias.

## 3) Clases Java con mismo simpleName
- No se detectaron duplicados.

## 4) Plantillas Thymeleaf duplicadas por nombre
- `dashboard.html`: `src/main/resources/templates/admin/dashboard.html`, `src/main/resources/templates/admin/reportes/dashboard.html`, `src/main/resources/templates/gestor/dashboard.html`
- `form.html`: `src/main/resources/templates/gestor/mascota/form.html`, `src/main/resources/templates/gestor/mascotas/form.html`, `src/main/resources/templates/mascota/form.html`
- `lista.html`: `src/main/resources/templates/gestor/mascota/lista.html`, `src/main/resources/templates/mascota/lista.html`
- `login.html`: `src/main/resources/templates/login.html`, `src/main/resources/templates/usuario/login.html`, `src/main/resources/templates/usuarios/login.html`
- `mascotas.html`: `src/main/resources/templates/admin/mascotas.html`, `src/main/resources/templates/admin/reportes/mascotas.html`, `src/main/resources/templates/gestor/mascotas.html`
- `usuarios.html`: `src/main/resources/templates/admin/reportes/usuarios.html`, `src/main/resources/templates/admin/usuarios.html`
- Riesgo runtime: medio/alto si algún controlador retorna nombre de vista ambiguo.

## 5) Recursos estáticos duplicados
- Por nombre en `static/`: no detectados.
- Por contenido: ver sección 2.

## 6) `.classpath`
- No existe `.classpath` en el repositorio; no se pueden detectar entradas duplicadas desde Git.
- Acción en STS/Eclipse: revisar Build Path y remover `target/` y `generated-sources/` si aparecen como source folders.

## 7) Duplicación de beans
- No se detectaron colisiones de nombre de bean por estereotipos.

## 8) Duplicación de mappings
- No se detectaron colisiones exactas de `método HTTP + ruta`.

## 9) Tests duplicados
- No se detectaron duplicados por nombre en `src/test`.

## 10) Duplicados con potencial de conflicto runtime
1. **Alto**: templates con mismo nombre (`login.html`, `form.html`, `dashboard.html`, etc.).
2. **Medio**: imágenes duplicadas por contenido entre `static/images`, `static/uploads` y `uploads`.

## 11) Estructura ideal recomendada
```text
src/main/resources/templates/
  auth/login.html
  admin/dashboard.html
  admin/reportes/dashboard-reportes.html
  mascota/formulario.html
  mascota/listado.html

src/main/resources/static/
  images/mascotas/
  css/
  js/

uploads/   # runtime, fuera de resources y preferible fuera del repo
```
## 12) Comandos de verificación local
### Linux/macOS
```bash
python3 scripts/audit_duplicates.py | jq .
```
```bash
find . -type f | sed "s#^.*/##" | sort | uniq -d
```
```bash
find . -type f -not -path "*/.git/*" -print0 | xargs -0 sha256sum | sort
```
### PowerShell
```powershell
python .\scripts\audit_duplicates.py | ConvertFrom-Json
```
```powershell
Get-ChildItem -Recurse -File | Group-Object Name | ? Count -gt 1
```
## 13) Plan de corrección
1. Backup y rama de trabajo.
2. Unificar/renombrar templates duplicados y actualizar controladores.
3. Deduplicar imágenes por hash y corregir referencias en BD/código.
4. Ejecutar `mvn clean test` y smoke test manual de rutas críticas.
5. En STS/Eclipse: `Maven -> Update Project` y `Project -> Clean`.
6. Empaquetar: `mvn clean package`.
