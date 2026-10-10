package com.grupo05.negociar.model.entidades.catalogo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ubicaciones", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Ubicacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long idUbicacion;

    @NotBlank
    @Size(max = 30)
    @Column(name = "codigo", length = 30, nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @NotBlank
    @Size(max = 30)
    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo = "ALMACEN";

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
}
