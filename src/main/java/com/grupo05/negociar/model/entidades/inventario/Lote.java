package com.grupo05.negociar.model.entidades.inventario;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
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

@Entity
@Table(name = "lotes", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Lote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @NotBlank
    @Size(max = 80)
    @Column(name = "codigo_lote", length = 80, nullable = false)
    private String codigoLote;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_recepcion", nullable = false, updatable = false)
    private OffsetDateTime fechaRecepcion = OffsetDateTime.now();

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
