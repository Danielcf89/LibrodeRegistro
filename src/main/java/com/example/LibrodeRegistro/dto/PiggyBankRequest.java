package com.example.LibrodeRegistro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PiggyBankRequest {
    private String nombre;
    private BigDecimal meta;
    private LocalDate fechaLimite;
    private boolean ahorroLibre = true;
    private BigDecimal montoRecomendadoSemanal; // Agrega este campo
}

