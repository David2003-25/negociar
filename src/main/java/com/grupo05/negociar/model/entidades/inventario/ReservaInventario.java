package com.grupo05.negociar.model.entidades.inventario;

import com.grupo05.negociar.model.entidades.distribucion.DetallePicking;
import com.grupo05.negociar.model.enums.EstadoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reservas_inventario", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class ReservaInventario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_existencia", nullable = false)
    private Existencia existencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_picking")
    private DetallePicking detallePicking;

    @NotNull
    @Min(1)
    @Column(name = "cantidad", nullable = false)
    private Long cantidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoReserva estado = EstadoReserva.ACTIVA;

    @Column(name = "fecha_reserva", nullable = false, updatable = false)
    private OffsetDateTime fechaReserva = OffsetDateTime.now();

    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;
}
