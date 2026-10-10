package com.grupo05.negociar.service.auditoria;

import java.time.OffsetDateTime;

public interface ReporteService {

    byte[] exportarExistenciasCsv();

    byte[] exportarMovimientosCsv(OffsetDateTime desde, OffsetDateTime hasta);
}
