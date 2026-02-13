# Auditoría técnica de duplicados (Spring Boot + Maven + STS/Eclipse)

Fecha: 2026-02-13  
Proyecto analizado: `LovePawsSpringBoot`

## Alcance y método
Se revisó el proyecto completo para detectar duplicidades por:

- nombre de archivo repetido en rutas distintas
- contenido idéntico (copias exactas por hash SHA-256)
- clases Java con mismo `simpleName` en paquetes distintos
- plantillas Thymeleaf duplicadas por nombre
- recursos estáticos duplicados
- entradas duplicadas en `.classpath`
- beans/componentes con mismo nombre lógico de bean
- mappings de Spring repetidos (`método HTTP + ruta`)
- tests duplicados
- inclusión de `target`/`generated-sources` como source folder

## Hallazgos

### 1) Duplicados por nombre de archivo (reales)

1. `login.html`
   - `src/main/resources/templates/login.html`
   - `src/main/resources/templates/usuario/login.html`
   - `src/main/resources/templates/usuarios/login.html`
   - Impacto potencial: alta confusión de mantenimiento; riesgo de renderizar template equivocado si en controladores se usa nombre no calificado.
   - Recomendación: conservar una convención única por módulo (`usuario/login`, `admin/login`, etc.) y eliminar/copiar lógica en un solo archivo base con fragmentos.

2. `dashboard.html`
   - `src/main/resources/templates/admin/dashboard.html`
   - `src/main/resources/templates/admin/reportes/dashboard.html`
   - `src/main/resources/templates/gestor/dashboard.html`
   - Impacto: ambigüedad funcional y deuda técnica.
   - Recomendación: renombrar vistas de reportes a `dashboard-reportes.html` o mover bajo estructura por feature.

3. `mascotas.html`
   - `src/main/resources/templates/admin/mascotas.html`
   - `src/main/resources/templates/admin/reportes/mascotas.html`
   - `src/main/resources/templates/gestor/mascotas.html`
   - Impacto: riesgo de mapeos de vista poco explícitos.
   - Recomendación: prefijos por contexto (`admin-mascotas`, `gestor-mascotas`, `reportes-mascotas`).

4. `form.html`
   - `src/main/resources/templates/gestor/mascota/form.html`
   - `src/main/resources/templates/gestor/mascotas/form.html`
   - `src/main/resources/templates/mascota/form.html`
   - Impacto: alta probabilidad de usar template incorrecto en refactors.
   - Recomendación: unificar singular/plural (`mascotas`) y dejar solo un formulario por caso de uso.

5. `lista.html`
   - `src/main/resources/templates/gestor/mascota/lista.html`
   - `src/main/resources/templates/mascota/lista.html`
   - Impacto: duplicidad de UX y esfuerzo de mantenimiento.
   - Recomendación: consolidar y parametrizar por rol.

6. `usuarios.html`
   - `src/main/resources/templates/admin/reportes/usuarios.html`
   - `src/main/resources/templates/admin/usuarios.html`
   - Impacto: posible inconsistencia de datos/columnas.
   - Recomendación: separar intención (`usuarios-listado` vs `usuarios-reporte`) o unificar.

---

### 2) Duplicados por contenido idéntico (copias exactas)

Se detectaron copias exactas (mismo hash) principalmente en imágenes entre:
- `src/main/resources/static/images/mascotas/*`
- `src/main/resources/static/uploads/*`
- `uploads/*`

Casos representativos:
- `src/main/resources/static/images/mascotas/daisy.jpg`
- `src/main/resources/static/uploads/03177191-f505-4002-94b4-245236634802.jpg`
- `src/main/resources/static/uploads/e3b32964-be83-4ad5-b663-3689d5fffb4f.jpg`

Y también:
- `src/main/resources/static/images/mascotas/bruno.jpg`
- `src/main/resources/static/images/mascotas/pp09.jpg`

Impacto potencial:
- incremento innecesario del tamaño del repo y artefacto
- dificultad para versionar activos
- posible contenido “huérfano” o repetido por carga de archivos

Recomendación:
- elegir **una** estrategia: assets seed (`static/images/mascotas`) **o** uploads runtime (`uploads` fuera de classpath)
- deduplicar por hash y actualizar referencias en BD/entidades
- excluir `uploads/` del versionado si es runtime

---

### 3) Clases Java con mismo nombre simple

Resultado: **no se detectaron duplicados** de `simpleName` entre clases Java de `src/main/java`.

Impacto actual: sin conflicto por nombre de clase.

Recomendación:
- mantener sufijos semánticos (`*Controller`, `*ServiceImpl`, `*Repository`) para evitar colisiones futuras.

---

### 4) Plantillas Thymeleaf duplicadas por nombre

Coinciden con el bloque de duplicados por nombre (sección 1):
`login.html`, `dashboard.html`, `mascotas.html`, `form.html`, `lista.html`, `usuarios.html`.

Riesgo runtime:
- si un controlador retorna vistas relativas ambiguas, se puede renderizar una plantilla no esperada.

Recomendación:
- usar nombres de vista explícitos y completos (`"admin/reportes/usuarios"`) en todos los controladores
- aplicar convención única de carpetas por bounded context.

---

### 5) Recursos estáticos duplicados

No se detectaron duplicados por **nombre de archivo** dentro de `static/` en rutas distintas.  
Sí se detectaron duplicados por **contenido** (ver sección 2).

---

### 6) Entradas duplicadas en `.classpath`

Resultado: no existe archivo `.classpath` en el repositorio analizado.

Impacto:
- no se puede validar duplicados de classpath directamente desde Git.

Recomendación:
- exportar `.classpath` local de STS/Eclipse y validar con script (ver sección comandos).

---

### 7) Duplicación de beans / componentes (mismo nombre de bean)

Resultado: **no se detectaron** colisiones de nombres de bean por convención Spring (`@Controller/@Service/@Repository/@Component`) en código fuente.

Riesgo residual:
- podría aparecer si se define `@Bean(name="...")` duplicado en configuraciones futuras.

Recomendación:
- mantener nombres explícitos si se usan factories `@Bean`.

---

### 8) Duplicación de mappings Spring (`HTTP + ruta`)

Resultado: **no se detectaron** rutas duplicadas exactas por combinación de método HTTP y path.

Riesgo residual:
- sobrecargas con `params`, `consumes`, `produces` podrían requerir validación adicional con `actuator/mappings` en runtime.

Recomendación:
- habilitar `spring-boot-starter-actuator` + endpoint `/actuator/mappings` en entorno de QA para validación final.

---

### 9) Tests duplicados/copia en `src/test`

Resultado: no se detectaron duplicados por nombre ni por contenido en `src/test`.

---

### 10) Source folders inválidos (`target` / `generated-sources`)

No se encontró `.classpath`; por tanto, no hay evidencia en repo de inclusión errónea como source folder.

Recomendación en STS/Eclipse:
- verificar `Build Path` y remover cualquier `target/` o `generated-sources/` agregado manualmente.

## Duplicados con potencial de conflicto en runtime

Prioridad alta:
1. Plantillas Thymeleaf duplicadas por nombre (`login.html`, `form.html`, etc.) por ambigüedad y errores de renderizado.
2. Activos estáticos/imagenes duplicadas por contenido (impacto de tamaño y referencias inconsistentes).

Prioridad media:
3. Estructura duplicada singular/plural (`mascota` vs `mascotas`) en templates.

## Estructura ideal recomendada

```text
src/
  main/
    java/com/lovepaws/app/
      config/
      modules/
        admin/
          controller/
          service/
          repository/
        mascota/
        adopcion/
        usuario/
    resources/
      templates/
        admin/
          dashboard.html
          usuarios-listado.html
          reportes/
            dashboard-reportes.html
            usuarios-reporte.html
        mascota/
          listado.html
          formulario.html
        auth/
          login.html
      static/
        css/
        js/
        images/
          mascotas/
  test/
    java/
uploads/   (fuera de src/main/resources y fuera del repo idealmente)
```

Reglas sugeridas:
- evitar singular/plural mixto en rutas de carpeta
- un nombre de template por caso de uso
- assets runtime fuera de classpath

## Comandos/scripts para validar localmente

### Linux/macOS (bash)

1) Duplicados por nombre de archivo
```bash
find . -type f | sed 's#^.*/##' | sort | uniq -d
```

2) Duplicados por contenido (hash)
```bash
find . -type f -not -path '*/.git/*' -print0 \
  | xargs -0 sha256sum \
  | sort \
  | awk '{print $1}' | uniq -d
```

3) Clases Java con mismo simpleName
```bash
find src/main/java -name '*.java' -print \
 | awk -F/ '{print $NF}' \
 | sed 's/.java$//' | sort | uniq -d
```

4) Templates duplicados por nombre
```bash
find src/main/resources/templates -type f -name '*.html' \
 | awk -F/ '{print $NF}' | sort | uniq -d
```

5) Mappings repetidos (heurístico)
```bash
rg '@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)' src/main/java
```

6) `.classpath` con entradas repetidas (si existe)
```bash
[ -f .classpath ] && xmllint --xpath '//classpathentry/@path' .classpath
```

### PowerShell

1) Duplicados por nombre
```powershell
Get-ChildItem -Recurse -File | Group-Object Name | Where-Object Count -gt 1
```

2) Duplicados por contenido (hash)
```powershell
Get-ChildItem -Recurse -File |
  Get-FileHash -Algorithm SHA256 |
  Group-Object Hash |
  Where-Object Count -gt 1
```

3) Clases Java por simpleName duplicado
```powershell
Get-ChildItem src/main/java -Recurse -Filter *.java |
  Group-Object BaseName |
  Where-Object Count -gt 1
```

4) Templates duplicados por nombre
```powershell
Get-ChildItem src/main/resources/templates -Recurse -File -Filter *.html |
  Group-Object Name |
  Where-Object Count -gt 1
```

## Plan de corrección recomendado (paso a paso)

1. **Backup / rama de trabajo**
   - crear rama `chore/dedup-audit-fixes`
   - respaldar `templates/` y cualquier carpeta de imágenes

2. **Unificar templates duplicados**
   - definir naming final
   - mover/renombrar
   - actualizar todos los `return "..."` en controladores

3. **Deduplicar imágenes por hash**
   - seleccionar archivo canónico
   - actualizar referencias en BD/código
   - eliminar copias

4. **Validar mappings y vistas**
   - `mvn clean test`
   - correr aplicación y smoke test de rutas clave

5. **Higiene STS/Eclipse**
   - `Maven -> Update Project`
   - `Project -> Clean`
   - revisar `Build Path` (sin `target/generated-sources`)

6. **Empaquetado final**
   - `mvn clean package`
   - verificar artefacto y tamaño final

## Comandos usados en esta auditoría (ejecutados)

- `find .. -name AGENTS.md -print`
- `git status --short`
- `find . -maxdepth 3 -type d | sort`
- script Python para duplicados por nombre/hash/simpleName/templates/static/test
- script Python para colisiones de mappings y bean names

