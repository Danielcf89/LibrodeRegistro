package com.example.LibrodeRegistro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class PiggyMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private LocalDate date;

    private String description;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piggy_bank_id")
    private PiggyBank piggyBank;


}


