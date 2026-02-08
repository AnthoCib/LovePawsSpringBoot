package com.lovepaws.app.adopcion.domain;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.user.domain.Usuario;

import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_adopcion",
       uniqueConstraints = @UniqueConstraint(name = "ux_solicitud_usuario_mascota", columnNames = {"id_usuario", "mascota_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE solicitud_adopcion SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class SolicitudAdopcion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "mascota_id")
	private Mascota mascota;

	@CreationTimestamp
	@Column(name = "fecha_solicitud")
	private LocalDateTime fechaSolicitud;

	@Column(name = "info_adicional", columnDefinition = "TEXT")
	private String infoAdicional;

	@Column(name = "pq_adoptar", columnDefinition = "TEXT")
	private String pqAdoptar;

	@Column(name = "tiempo_dedicado", columnDefinition = "TEXT")
	private String tiempoDedicado;

	@Column(name = "cubrir_costos", columnDefinition = "TEXT")
	private String cubrirCostos;

	@Column(name = "plan_mascota", columnDefinition = "TEXT")
	private String planMascota;

	@Column(name = "tipo_vivienda")
	private String tipoVivienda;

	@Column(name = "experiencia")
	private String experiencia;

	@Column(name = "ninos_otra_mascotas")
	private String ninosOtraMascotas;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoAdopcion estado;

	@UpdateTimestamp
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
