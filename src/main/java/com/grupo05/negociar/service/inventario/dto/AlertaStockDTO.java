package com.grupo05.negociar.service.inventario.dto;

import com.grupo05.negociar.model.entidades.catalogo.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaStockDTO {

    private Producto producto;
    private Long stockActual;
    private Integer stockMinimo;
    private String nivelAlerta;
}
