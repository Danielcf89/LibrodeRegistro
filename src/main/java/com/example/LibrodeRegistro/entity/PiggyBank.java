package com.example.LibrodeRegistro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class PiggyBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private BigDecimal meta;

    private LocalDate fechaLimite;

    private boolean ahorroLibre = true; // true: el usuario deposita cuando quiera

    private BigDecimal montoRecomendadoSemanal; // si es planificado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "piggyBank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PiggyMovement> movimientos;

    // Métodos auxiliares para cálculo
    public BigDecimal getTotalAhorrado() {
        if (movimientos == null || movimientos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return movimientos.stream()
                .map(PiggyMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontoFaltante() {
        return meta.subtract(getTotalAhorrado());
    }

    public long getSemanasRestantes() {
        return java.time.temporal.ChronoUnit.WEEKS.between(LocalDate.now(), fechaLimite);
    }
}
