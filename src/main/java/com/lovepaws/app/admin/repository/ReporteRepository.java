package com.lovepaws.app.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lovepaws.app.admin.dto.MascotaPorEstadoDTO;
import com.lovepaws.app.admin.dto.UsuarioPorRolDTO;
import com.lovepaws.app.mascota.domain.Mascota;

@Repository
public interface ReporteRepository extends JpaRepository<Mascota, Integer> {

    @Query("""
            SELECT new com.lovepaws.app.admin.dto.MascotaPorEstadoDTO(
                m.estado.id,
                COUNT(m)
            )
            FROM Mascota m
            GROUP BY m.estado.id
            """)
    List<MascotaPorEstadoDTO> mascotasPorEstado();

    @Query("""
            SELECT new com.lovepaws.app.admin.dto.MascotaPorEstadoDTO(
                m.estado.id,
                COUNT(m)
            )
            FROM Mascota m
            WHERE m.fechaCreacion >= :desde
              AND m.fechaCreacion < :hasta
            GROUP BY m.estado.id
            """)
    List<MascotaPorEstadoDTO> mascotasPorEstado(LocalDateTime desde, LocalDateTime hasta);

    @Query("""
            SELECT new com.lovepaws.app.admin.dto.UsuarioPorRolDTO(
                u.rol.nombre,
                COUNT(u)
            )
            FROM Usuario u
            GROUP BY u.rol.nombre
            """)
    List<UsuarioPorRolDTO> usuariosPorRol();

    @Query("""
            SELECT new com.lovepaws.app.admin.dto.UsuarioPorRolDTO(
                u.rol.nombre,
                COUNT(u)
            )
            FROM Usuario u
            WHERE u.fechaCreacion >= :desde
              AND u.fechaCreacion < :hasta
            GROUP BY u.rol.nombre
            """)
    List<UsuarioPorRolDTO> usuariosPorRol(LocalDateTime desde, LocalDateTime hasta);
}
