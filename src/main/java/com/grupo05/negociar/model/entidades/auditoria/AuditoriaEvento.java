package com.grupo05.negociar.model.entidades.auditoria;

import com.grupo05.negociar.model.entidades.seguridad.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_eventos", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaEvento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @NotBlank
    @Size(max = 100)
    @Column(name = "entidad", length = 100, nullable = false)
    private String entidad;

    @Size(max = 100)
    @Column(name = "id_registro", length = 100)
    private String idRegistro;

    @NotBlank
    @Size(max = 30)
    @Column(name = "accion", length = 30, nullable = false)
    private String accion;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private OffsetDateTime fechaEvento = OffsetDateTime.now();

    @Column(name = "detalle", columnDefinition = "text")
    private String detalle;

    @Column(name = "datos_anteriores", columnDefinition = "text")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "text")
    private String datosNuevos;
}
