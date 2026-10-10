package com.grupo05.negociar.model.entidades.catalogo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "productos", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank
    @Size(max = 50)
    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotBlank
    @Size(max = 80)
    @Column(name = "unidad_presentacion", length = 80, nullable = false)
    private String unidadPresentacion;

    @NotNull
    @Min(0)
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<ProductoProveedor> productosProveedores = new HashSet<>();
}
