package com.lovepaws.app.adopcion.domain;

import com.lovepaws.app.user.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuesta_seguimiento_adoptante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE respuesta_seguimiento_adoptante SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class RespuestaSeguimientoAdoptante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seguimiento_id")
    private SeguimientoPostAdopcion seguimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopcion_id")
    private Adopcion adopcion;

    @CreationTimestamp
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "estado_salud")
    private String estadoSalud;

    private String comportamiento;

    private String alimentacion;

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    @Column(name = "foto_url")
    private String fotoUrl;

    private Boolean revisado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion")
    private Usuario usuarioCreacion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
