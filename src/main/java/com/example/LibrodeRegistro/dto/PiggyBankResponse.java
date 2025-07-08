package com.example.LibrodeRegistro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PiggyBankResponse {
    private Long id;
    private String nombre;
    private BigDecimal meta;
    private BigDecimal totalAhorrado;
    private BigDecimal montoFaltante;
    private long semanasRestantes;
    private LocalDate fechaLimite;
    private boolean ahorroLibre;
    private BigDecimal montoRecomendadoSemanal;
    private List<PiggyMovementResponse> movimientos;
}


