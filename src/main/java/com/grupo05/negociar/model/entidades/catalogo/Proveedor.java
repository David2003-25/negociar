package com.grupo05.negociar.model.entidades.catalogo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "proveedores", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Proveedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @NotBlank
    @Size(max = 150)
    @Column(name = "razon_social", length = 150, nullable = false)
    private String razonSocial;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(name = "ruc", length = 11, nullable = false, unique = true)
    private String ruc;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Email
    @Size(max = 150)
    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "direccion", columnDefinition = "text")
    private String direccion;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private Set<ProductoProveedor> productosProveedores = new HashSet<>();
}
