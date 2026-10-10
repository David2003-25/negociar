package com.grupo05.negociar.model.entidades.seguridad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permisos", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Permiso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @NotBlank
    @Size(max = 80)
    @Column(name = "nombre", length = 80, nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();
}
