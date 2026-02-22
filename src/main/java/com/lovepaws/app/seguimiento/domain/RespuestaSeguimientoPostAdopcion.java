package com.lovepaws.app.seguimiento.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.user.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "respuesta_seguimiento_adoptante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE respuesta_seguimiento_adoptante SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class RespuestaSeguimientoPostAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seguimiento_id", nullable = false)
    private SeguimientoPostAdopcion seguimiento;

    @Column(name = "comentarios", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "revisado")
    private Boolean revisado = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion")
    private Usuario usuarioCreacion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
