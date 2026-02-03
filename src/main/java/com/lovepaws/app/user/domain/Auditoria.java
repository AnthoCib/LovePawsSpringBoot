package com.lovepaws.app.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tabla;

    @Column(name = "id_registro")
    private Integer idRegistro;

    private String operacion;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "usuario_nombre")
    private String usuarioNombre;

    @CreationTimestamp
    private LocalDateTime fecha;

    private String detalle;
}
