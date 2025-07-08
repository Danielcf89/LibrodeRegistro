package com.example.LibrodeRegistro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PiggyMovementDto {
    private BigDecimal amount;
    private LocalDate date;
    private String description;
}

