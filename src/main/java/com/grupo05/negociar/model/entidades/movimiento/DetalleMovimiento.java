package com.grupo05.negociar.model.entidades.movimiento;

import com.grupo05.negociar.model.entidades.inventario.Existencia;
import com.grupo05.negociar.model.enums.OperacionDetalle;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "detalle_movimiento", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class DetalleMovimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_movimiento")
    private Long idDetalleMovimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private MovimientoInventario movimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_existencia", nullable = false)
    private Existencia existencia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operacion", length = 10, nullable = false)
    private OperacionDetalle operacion;

    @NotNull
    @Min(1)
    @Column(name = "cantidad", nullable = false)
    private Long cantidad;
}
