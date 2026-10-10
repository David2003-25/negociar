package com.grupo05.negociar.model.entidades.distribucion;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "detalle_solicitud", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class DetalleSolicitud implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_solicitud")
    private Long idDetalleSolicitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudDistribucion solicitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull
    @Min(1)
    @Column(name = "cantidad_solicitada", nullable = false)
    private Long cantidadSolicitada;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;
}
