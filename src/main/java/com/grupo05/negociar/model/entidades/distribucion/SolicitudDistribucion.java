package com.grupo05.negociar.model.entidades.distribucion;

import com.grupo05.negociar.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "solicitudes_distribucion", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudDistribucion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @NotBlank
    @Size(max = 30)
    @Column(name = "codigo_solicitud", length = 30, nullable = false, unique = true)
    private String codigoSolicitud;

    @NotBlank
    @Size(max = 200)
    @Column(name = "destino", length = 200, nullable = false)
    private String destino;

    @Size(max = 150)
    @Column(name = "responsable", length = 150)
    private String responsable;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private OffsetDateTime fechaSolicitud = OffsetDateTime.now();

    @Column(name = "fecha_requerida")
    private LocalDate fechaRequerida;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetalleSolicitud> detalles = new HashSet<>();

    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY)
    private Set<Picking> pickings = new HashSet<>();
}
