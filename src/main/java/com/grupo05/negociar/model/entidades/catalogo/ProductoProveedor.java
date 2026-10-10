package com.grupo05.negociar.model.entidades.catalogo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "productos_proveedores", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class ProductoProveedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_proveedor")
    private Long idProductoProveedor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @Size(max = 80)
    @Column(name = "codigo_producto_proveedor", length = 80)
    private String codigoProductoProveedor;

    @Column(name = "precio_referencia", precision = 12, scale = 2)
    private BigDecimal precioReferencia;

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro = OffsetDateTime.now();
}
