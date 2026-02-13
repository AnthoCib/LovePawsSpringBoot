package com.lovepaws.app.mascota.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.mascota.dto.CategoriaRequestDTO;
import com.lovepaws.app.mascota.dto.CategoriaResponseDTO;
import com.lovepaws.app.mascota.dto.EspecieRequestDTO;
import com.lovepaws.app.mascota.dto.EspecieResponseDTO;
import com.lovepaws.app.mascota.dto.RazaRequestDTO;
import com.lovepaws.app.mascota.dto.RazaResponseDTO;
import com.lovepaws.app.mascota.service.CatalogoMascotaService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
@Validated
public class CatalogoMascotaApiController {

    private final CatalogoMascotaService catalogoMascotaService;

    @GetMapping("/especies")
    public List<EspecieResponseDTO> listarEspecies() {
        return catalogoMascotaService.listarEspecies();
    }

    @GetMapping("/especies/{id}")
    public EspecieResponseDTO obtenerEspecie(@PathVariable Integer id) {
        return catalogoMascotaService.obtenerEspecie(id);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/especies")
    @ResponseStatus(HttpStatus.CREATED)
    public EspecieResponseDTO crearEspecie(@Valid @RequestBody EspecieRequestDTO request, Authentication authentication) {
        return catalogoMascotaService.crearEspecie(request, usuarioAutenticadoId(authentication));
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PutMapping("/especies/{id}")
    public EspecieResponseDTO actualizarEspecie(@PathVariable Integer id, @Valid @RequestBody EspecieRequestDTO request) {
        return catalogoMascotaService.actualizarEspecie(id, request);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @DeleteMapping("/especies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEspecie(@PathVariable Integer id) {
        catalogoMascotaService.eliminarEspecie(id);
    }

    @GetMapping("/razas")
    public List<RazaResponseDTO> listarRazas(@RequestParam(required = false) Integer especieId) {
        return catalogoMascotaService.listarRazas(especieId);
    }

    @GetMapping("/razas/{id}")
    public RazaResponseDTO obtenerRaza(@PathVariable Integer id) {
        return catalogoMascotaService.obtenerRaza(id);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/razas")
    @ResponseStatus(HttpStatus.CREATED)
    public RazaResponseDTO crearRaza(@Valid @RequestBody RazaRequestDTO request, Authentication authentication) {
        return catalogoMascotaService.crearRaza(request, usuarioAutenticadoId(authentication));
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PutMapping("/razas/{id}")
    public RazaResponseDTO actualizarRaza(@PathVariable Integer id, @Valid @RequestBody RazaRequestDTO request) {
        return catalogoMascotaService.actualizarRaza(id, request);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @DeleteMapping("/razas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarRaza(@PathVariable Integer id) {
        catalogoMascotaService.eliminarRaza(id);
    }

    @GetMapping("/categorias")
    public List<CategoriaResponseDTO> listarCategorias() {
        return catalogoMascotaService.listarCategorias();
    }

    @GetMapping("/categorias/{id}")
    public CategoriaResponseDTO obtenerCategoria(@PathVariable Integer id) {
        return catalogoMascotaService.obtenerCategoria(id);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponseDTO crearCategoria(@Valid @RequestBody CategoriaRequestDTO request,
                                               Authentication authentication) {
        return catalogoMascotaService.crearCategoria(request, usuarioAutenticadoId(authentication));
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PutMapping("/categorias/{id}")
    public CategoriaResponseDTO actualizarCategoria(@PathVariable Integer id,
                                                    @Valid @RequestBody CategoriaRequestDTO request) {
        return catalogoMascotaService.actualizarCategoria(id, request);
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @DeleteMapping("/categorias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCategoria(@PathVariable Integer id) {
        catalogoMascotaService.eliminarCategoria(id);
    }

    private Integer usuarioAutenticadoId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            return principal.getUsuario().getId();
        }
        return null;
    }
}
