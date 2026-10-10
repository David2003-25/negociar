package com.grupo05.negociar.model.entidades.distribucion;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.enums.EstadoDetallePicking;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "detalle_picking", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class DetallePicking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_picking")
    private Long idDetallePicking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_picking", nullable = false)
    private Picking picking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_detalle_solicitud", nullable = false)
    private DetalleSolicitud detalleSolicitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_existencia", nullable = false)
    private Existencia existencia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull
    @Min(1)
    @Column(name = "cantidad_requerida", nullable = false)
    private Long cantidadRequerida;

    @NotNull
    @Min(0)
    @Column(name = "cantidad_recogida", nullable = false)
    private Long cantidadRecogida = 0L;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoDetallePicking estado = EstadoDetallePicking.PENDIENTE;
}
