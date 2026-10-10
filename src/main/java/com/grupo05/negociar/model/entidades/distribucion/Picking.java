package com.grupo05.negociar.model.entidades.distribucion;

import com.grupo05.negociar.model.enums.EstadoPicking;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pickings", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Picking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_picking")
    private Long idPicking;

    @NotBlank
    @Size(max = 30)
    @Column(name = "codigo_picking", length = 30, nullable = false, unique = true)
    private String codigoPicking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudDistribucion solicitud;

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private OffsetDateTime fechaInicio = OffsetDateTime.now();

    @Column(name = "fecha_confirmacion")
    private OffsetDateTime fechaConfirmacion;

    @Size(max = 150)
    @Column(name = "responsable", length = 150)
    private String responsable;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoPicking estado = EstadoPicking.PENDIENTE;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;

    @OneToMany(mappedBy = "picking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetallePicking> detalles = new HashSet<>();
}
