package com.lovepaws.app.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovepaws.app.mascota.dto.CategoriaRequestDTO;
import com.lovepaws.app.mascota.dto.EspecieRequestDTO;
import com.lovepaws.app.mascota.dto.EspecieResponseDTO;
import com.lovepaws.app.mascota.dto.RazaRequestDTO;
import com.lovepaws.app.mascota.service.CatalogoMascotaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/catalogos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogoController {

    private final CatalogoMascotaService catalogoMascotaService;

    @GetMapping
    public String vistaCatalogos(@RequestParam(required = false) Integer especieId, Model model) {
        List<EspecieResponseDTO> especies = catalogoMascotaService.listarEspecies();
        model.addAttribute("especies", especies);
        model.addAttribute("categorias", catalogoMascotaService.listarCategorias());

        Integer especieFiltro = especieId;
        if (especieFiltro == null && !especies.isEmpty()) {
            especieFiltro = especies.get(0).id();
        }
        model.addAttribute("especieSeleccionada", especieFiltro);
        model.addAttribute("razas", catalogoMascotaService.listarRazas(especieFiltro));
        return "admin/catalogos";
    }

    @PostMapping("/especies")
    public String guardarEspecie(@RequestParam(required = false) Integer id,
                                 @RequestParam String nombre,
                                 @RequestParam(defaultValue = "true") Boolean estado,
                                 RedirectAttributes ra) {
        try {
            if (id == null) {
                catalogoMascotaService.crearEspecie(new EspecieRequestDTO(nombre, estado), null);
                ra.addFlashAttribute("ok", "Especie creada correctamente");
            } else {
                catalogoMascotaService.actualizarEspecie(id, new EspecieRequestDTO(nombre, estado));
                ra.addFlashAttribute("ok", "Especie actualizada correctamente");
            }
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/especies/{id}/eliminar")
    public String eliminarEspecie(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            catalogoMascotaService.eliminarEspecie(id);
            ra.addFlashAttribute("ok", "Especie eliminada correctamente");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/categorias")
    public String guardarCategoria(@RequestParam(required = false) Integer id,
                                   @RequestParam String nombre,
                                   @RequestParam(required = false) String descripcion,
                                   @RequestParam(defaultValue = "true") Boolean estado,
                                   RedirectAttributes ra) {
        try {
            if (id == null) {
                catalogoMascotaService.crearCategoria(new CategoriaRequestDTO(nombre, descripcion, estado), null);
                ra.addFlashAttribute("ok", "Categoría creada correctamente");
            } else {
                catalogoMascotaService.actualizarCategoria(id, new CategoriaRequestDTO(nombre, descripcion, estado));
                ra.addFlashAttribute("ok", "Categoría actualizada correctamente");
            }
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/categorias/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            catalogoMascotaService.eliminarCategoria(id);
            ra.addFlashAttribute("ok", "Categoría eliminada correctamente");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos";
    }

    @PostMapping("/razas")
    public String guardarRaza(@RequestParam(required = false) Integer id,
                              @RequestParam Integer especieId,
                              @RequestParam String nombre,
                              @RequestParam(defaultValue = "true") Boolean estado,
                              RedirectAttributes ra) {
        try {
            if (id == null) {
                catalogoMascotaService.crearRaza(new RazaRequestDTO(especieId, nombre, estado), null);
                ra.addFlashAttribute("ok", "Raza creada correctamente");
            } else {
                catalogoMascotaService.actualizarRaza(id, new RazaRequestDTO(especieId, nombre, estado));
                ra.addFlashAttribute("ok", "Raza actualizada correctamente");
            }
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos?especieId=" + especieId;
    }

    @PostMapping("/razas/{id}/eliminar")
    public String eliminarRaza(@PathVariable Integer id,
                               @RequestParam Integer especieId,
                               RedirectAttributes ra) {
        try {
            catalogoMascotaService.eliminarRaza(id);
            ra.addFlashAttribute("ok", "Raza eliminada correctamente");
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/catalogos?especieId=" + especieId;
    }
}
