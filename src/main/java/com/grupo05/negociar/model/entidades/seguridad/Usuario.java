package com.grupo05.negociar.model.entidades.seguridad;

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

@Entity
@Table(name = "usuarios", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombres", length = 100, nullable = false)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    @Column(name = "apellidos", length = 100, nullable = false)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "correo", length = 150, nullable = false, unique = true)
    private String correo;

    @NotBlank
    @Column(name = "password_hash", nullable = false, columnDefinition = "text")
    private String passwordHash;

    @NotNull
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @Column(name = "ultimo_acceso")
    private OffsetDateTime ultimoAcceso;
}
