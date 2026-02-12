# UI Governance Checklist

## Pull Request checklist (UI)
- [ ] ¿Se agregó `style="..."` inline? (si sí, justificar)
- [ ] ¿Se agregó `<style>` dentro de templates? (preferir CSS estático)
- [ ] ¿Se duplicó un selector global existente (`.btn-*`, `.lp-navbar`, `.sidebar`)?
- [ ] ¿Las imágenes nuevas están optimizadas (WebP/AVIF o compresión)?
- [ ] ¿Se respetó naming de assets (`kebab-case`, minúsculas, sin espacios)?

## Convenciones CSS
- Base: `/css/base`
- Componentes: `/css/components`
- Páginas: `/css/pages`
- Legacy temporal: `/css/legacy`

## Nota
`custom.css` es la fuente de verdad del tema global.
`custom-palette.css` queda como shim legacy temporal.
