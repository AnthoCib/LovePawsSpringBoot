package com.lovepaws.app.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovepaws.app.admin.domain.Categoria;
import com.lovepaws.app.admin.service.CategoriaService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/catalogos/categorias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogoCategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "admin/catalogos/categorias";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/catalogos/categoria-form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "admin/catalogos/categoria-form";
        }
        try {
            Integer userId = principal != null ? principal.getUsuario().getId() : null;
            categoriaService.crear(categoria, userId);
            ra.addFlashAttribute("swalSuccess", "Categoría creada correctamente");
            return "redirect:/admin/catalogos/categorias";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos/categorias/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("categoria", categoriaService.obtenerActiva(id));
            return "admin/catalogos/categoria-form";
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
            return "redirect:/admin/catalogos/categorias";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("categoria") Categoria categoria,
                             BindingResult bindingResult,
                             RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "admin/catalogos/categoria-form";
        }
        try {
            categoriaService.actualizar(id, categoria);
            ra.addFlashAttribute("swalSuccess", "Categoría actualizada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos/categorias";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            categoriaService.eliminar(id);
            ra.addFlashAttribute("swalSuccess", "Categoría eliminada correctamente");
        } catch (Exception ex) {
            ra.addFlashAttribute("swalError", ex.getMessage());
        }
        return "redirect:/admin/catalogos/categorias";
    }
}
