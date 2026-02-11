package com.lovepaws.app.adopcion.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.user.domain.Usuario;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_post_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE seguimiento_post_adopcion SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class SeguimientoPostAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adopcion_id")
    private Adopcion adopcion;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDateTime fechaVisita;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoMascota estado;


    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion")
    private Usuario usuarioCreacion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
