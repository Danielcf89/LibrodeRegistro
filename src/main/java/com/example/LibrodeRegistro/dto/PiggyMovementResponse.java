package com.example.LibrodeRegistro.dto;

import com.example.LibrodeRegistro.entity.MovementType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PiggyMovementResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private MovementType type;
    private String category;
}

