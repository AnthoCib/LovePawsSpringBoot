package com.lovepaws.app.mascota.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lovepaws.app.mascota.domain.Categoria;
import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.domain.Raza;
import com.lovepaws.app.mascota.dto.CategoriaRequestDTO;
import com.lovepaws.app.mascota.dto.CategoriaResponseDTO;
import com.lovepaws.app.mascota.dto.EspecieRequestDTO;
import com.lovepaws.app.mascota.dto.EspecieResponseDTO;
import com.lovepaws.app.mascota.dto.RazaRequestDTO;
import com.lovepaws.app.mascota.dto.RazaResponseDTO;
import com.lovepaws.app.mascota.repository.CategoriaRepository;
import com.lovepaws.app.mascota.repository.EspecieRepository;
import com.lovepaws.app.mascota.repository.RazaRepository;
import com.lovepaws.app.mascota.service.CatalogoMascotaService;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogoMascotaServiceImpl implements CatalogoMascotaService {

    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EspecieResponseDTO> listarEspecies() {
        return especieRepository.findAll().stream().map(this::toEspecieResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EspecieResponseDTO obtenerEspecie(Integer especieId) {
        return toEspecieResponse(findEspecieById(especieId));
    }

    @Override
    @Transactional
    public EspecieResponseDTO crearEspecie(EspecieRequestDTO request, Integer usuarioId) {
        String nombre = normalize(request.nombre());
        if (especieRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una especie con ese nombre");
        }

        Especie especie = new Especie();
        especie.setNombre(nombre);
        especie.setEstado(request.estado() != null ? request.estado() : Boolean.TRUE);
        especie.setIdUsuarioCreacion(resolveUsuarioId(usuarioId));

        return toEspecieResponse(especieRepository.save(especie));
    }

    @Override
    @Transactional
    public EspecieResponseDTO actualizarEspecie(Integer especieId, EspecieRequestDTO request) {
        Especie especie = findEspecieById(especieId);
        String nuevoNombre = normalize(request.nombre());

        if (!especie.getNombre().equalsIgnoreCase(nuevoNombre)
                && especieRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una especie con ese nombre");
        }

        especie.setNombre(nuevoNombre);
        especie.setEstado(request.estado() != null ? request.estado() : especie.getEstado());

        return toEspecieResponse(especieRepository.save(especie));
    }

    @Override
    @Transactional
    public void eliminarEspecie(Integer especieId) {
        Especie especie = findEspecieById(especieId);
        try {
            especieRepository.delete(especie);
            especieRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la especie porque tiene registros relacionados");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> listarRazas(Integer especieId) {
        if (especieId != null) {
            findEspecieById(especieId);
        }
        List<Raza> razas = especieId == null ? razaRepository.findAll() : razaRepository.findByEspecieId(especieId);
        return razas.stream().map(this::toRazaResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RazaResponseDTO obtenerRaza(Integer razaId) {
        return toRazaResponse(findRazaById(razaId));
    }

    @Override
    @Transactional
    public RazaResponseDTO crearRaza(RazaRequestDTO request, Integer usuarioId) {
        String nombre = normalize(request.nombre());
        if (razaRepository.existsByEspecieIdAndNombreIgnoreCase(request.especieId(), nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una raza con ese nombre para la especie indicada");
        }

        Especie especie = findEspecieById(request.especieId());

        Raza raza = new Raza();
        raza.setEspecie(especie);
        raza.setNombre(nombre);
        raza.setEstado(request.estado() != null ? request.estado() : Boolean.TRUE);
        raza.setUsuarioCreacion(findUsuarioById(usuarioId));

        return toRazaResponse(razaRepository.save(raza));
    }

    @Override
    @Transactional
    public RazaResponseDTO actualizarRaza(Integer razaId, RazaRequestDTO request) {
        Raza raza = findRazaById(razaId);
        String nuevoNombre = normalize(request.nombre());

        if ((!raza.getNombre().equalsIgnoreCase(nuevoNombre)
                || !raza.getEspecie().getId().equals(request.especieId()))
                && razaRepository.existsByEspecieIdAndNombreIgnoreCase(request.especieId(), nuevoNombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una raza con ese nombre para la especie indicada");
        }

        raza.setEspecie(findEspecieById(request.especieId()));
        raza.setNombre(nuevoNombre);
        raza.setEstado(request.estado() != null ? request.estado() : raza.getEstado());

        return toRazaResponse(razaRepository.save(raza));
    }

    @Override
    @Transactional
    public void eliminarRaza(Integer razaId) {
        Raza raza = findRazaById(razaId);
        try {
            razaRepository.delete(raza);
            razaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la raza porque tiene registros relacionados");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findAll().stream().map(this::toCategoriaResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoria(Integer categoriaId) {
        return toCategoriaResponse(findCategoriaById(categoriaId));
    }

    @Override
    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request, Integer usuarioId) {
        String nombre = normalize(request.nombre());
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(request.descripcion());
        categoria.setEstado(request.estado() != null ? request.estado() : Boolean.TRUE);
        categoria.setUsuarioCreacion(findUsuarioById(usuarioId));

        return toCategoriaResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Integer categoriaId, CategoriaRequestDTO request) {
        Categoria categoria = findCategoriaById(categoriaId);
        String nuevoNombre = normalize(request.nombre());

        if (!categoria.getNombre().equalsIgnoreCase(nuevoNombre)
                && categoriaRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(nuevoNombre);
        categoria.setDescripcion(request.descripcion());
        categoria.setEstado(request.estado() != null ? request.estado() : categoria.getEstado());

        return toCategoriaResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminarCategoria(Integer categoriaId) {
        Categoria categoria = findCategoriaById(categoriaId);
        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la categoría porque tiene registros relacionados");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private Integer resolveUsuarioId(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.existsById(usuarioId) ? usuarioId : null;
    }

    private Especie findEspecieById(Integer especieId) {
        return especieRepository.findById(especieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especie no encontrada"));
    }

    private Usuario findUsuarioById(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private Raza findRazaById(Integer razaId) {
        return razaRepository.findById(razaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raza no encontrada"));
    }

    private Categoria findCategoriaById(Integer categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    private EspecieResponseDTO toEspecieResponse(Especie especie) {
        return new EspecieResponseDTO(especie.getId(), especie.getNombre(), especie.getEstado(), especie.getIdUsuarioCreacion());
    }

    private RazaResponseDTO toRazaResponse(Raza raza) {
        Integer idUsuarioCreacion = raza.getUsuarioCreacion() != null ? raza.getUsuarioCreacion().getId() : null;
        return new RazaResponseDTO(
                raza.getId(),
                raza.getEspecie().getId(),
                raza.getEspecie().getNombre(),
                raza.getNombre(),
                raza.getEstado(),
                idUsuarioCreacion
        );
    }

    private CategoriaResponseDTO toCategoriaResponse(Categoria categoria) {
        Integer idUsuarioCreacion = categoria.getUsuarioCreacion() != null ? categoria.getUsuarioCreacion().getId() : null;
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado(),
                idUsuarioCreacion
        );
    }
}
