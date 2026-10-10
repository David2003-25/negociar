package com.grupo05.negociar.model.entidades.auditoria;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import com.grupo05.negociar.model.enums.EstadoNotificacion;
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
@Table(name = "notificaciones", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Notificacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @NotBlank
    @Size(max = 40)
    @Column(name = "tipo", length = 40, nullable = false)
    private String tipo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "destinatario", length = 150, nullable = false)
    private String destinatario;

    @NotBlank
    @Size(max = 200)
    @Column(name = "asunto", length = 200, nullable = false)
    private String asunto;

    @NotBlank
    @Column(name = "mensaje", columnDefinition = "text", nullable = false)
    private String mensaje;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @Column(name = "fecha_envio")
    private OffsetDateTime fechaEnvio;

    @NotNull
    @Column(name = "intentos", nullable = false)
    private Integer intentos = 0;

    @Column(name = "error_envio", columnDefinition = "text")
    private String errorEnvio;
}
