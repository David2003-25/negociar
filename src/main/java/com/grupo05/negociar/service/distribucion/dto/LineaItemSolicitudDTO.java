package com.grupo05.negociar.service.distribucion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineaItemSolicitudDTO {

    private Long idProducto;
    private Long cantidad;
    private String observacion;
}
