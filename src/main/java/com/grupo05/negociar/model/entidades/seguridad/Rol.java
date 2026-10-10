package com.grupo05.negociar.model.entidades.seguridad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Rol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nombre", length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roles_permisos",
            schema = "inventario",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<Permiso> permisos = new HashSet<>();

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();
}
