package com.grupo05.negociar.model.entidades.inventario;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.entidades.catalogo.Ubicacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "existencias", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Existencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_existencia")
    private Long idExistencia;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote lote;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    @NotNull
    @Min(0)
    @Column(name = "stock_fisico", nullable = false)
    private Long stockFisico = 0L;

    @NotNull
    @Min(0)
    @Column(name = "stock_reservado", nullable = false)
    private Long stockReservado = 0L;

    @Column(name = "stock_disponible", insertable = false, updatable = false)
    private Long stockDisponible;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion = OffsetDateTime.now();

    public Long getStockDisponible() {
        if (stockDisponible != null) {
            return stockDisponible;
        }
        return (stockFisico != null ? stockFisico : 0L) - (stockReservado != null ? stockReservado : 0L);
    }
}
