package com.grupo05.negociar.controller.auditoria;

import com.grupo05.negociar.service.auditoria.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public String mostrar() {
        return "reportes/reportes";
    }

    @GetMapping("/existencias/csv")
    public ResponseEntity<byte[]> exportarExistenciasCsv() {
        byte[] csv = reporteService.exportarExistenciasCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=existencias.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/movimientos/csv")
    public ResponseEntity<byte[]> exportarMovimientosCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        byte[] csv = reporteService.exportarMovimientosCsv(desde, hasta);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimientos.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
