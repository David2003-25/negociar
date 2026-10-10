package com.grupo05.negociar.model.entidades.movimiento;

import com.grupo05.negociar.model.entidades.catalogo.Proveedor;
import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import com.grupo05.negociar.model.enums.EstadoMovimiento;
import com.grupo05.negociar.model.enums.TipoMovimiento;
import jakarta.persistence.*;
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
@Table(name = "movimientos_inventario", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoInventario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", length = 20, nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private OffsetDateTime fechaMovimiento = OffsetDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoMovimiento estado = EstadoMovimiento.CONFIRMADO;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;

    @Size(max = 100)
    @Column(name = "documento_referencia", length = 100)
    private String documentoReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetalleMovimiento> detalles = new HashSet<>();

    @OneToOne(mappedBy = "movimiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AjusteTecnico ajusteTecnico;
}
