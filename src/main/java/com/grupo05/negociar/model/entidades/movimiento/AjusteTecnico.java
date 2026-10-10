package com.grupo05.negociar.model.entidades.movimiento;

import com.grupo05.negociar.model.entidades.seguridad.Usuario;
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
@Table(name = "ajustes_tecnicos", schema = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class AjusteTecnico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste")
    private Long idAjuste;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_movimiento", nullable = false, unique = true)
    private MovimientoInventario movimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Size(max = 250)
    @Column(name = "motivo", length = 250, nullable = false)
    private String motivo;

    @NotNull
    @Column(name = "cantidad_anterior", nullable = false)
    private Long cantidadAnterior;

    @NotNull
    @Column(name = "cantidad_nueva", nullable = false)
    private Long cantidadNueva;

    @Column(name = "fecha_ajuste", nullable = false, updatable = false)
    private OffsetDateTime fechaAjuste = OffsetDateTime.now();

    @Column(name = "observacion", columnDefinition = "text")
    private String observacion;
}
