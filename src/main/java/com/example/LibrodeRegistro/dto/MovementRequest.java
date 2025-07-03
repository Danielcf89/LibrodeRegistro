// MovementRequest.java
package com.example.LibrodeRegistro.dto;

import com.example.LibrodeRegistro.entity.MovementType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovementRequest {
    // Campos en inglés (originales)
    private BigDecimal amount;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    private String description;
    private MovementType type;
    private String category;

    // Campos en español (alias)
    @JsonProperty("cantidad")
    public void setCantidad(BigDecimal cantidad) {
        this.amount = cantidad;
    }

    @JsonProperty("fecha")
    @JsonFormat(pattern = "dd/MM/yyyy")
    public void setFecha(LocalDate fecha) {
        this.date = fecha;
    }

    @JsonProperty("descripcion")
    public void setDescripcion(String descripcion) {
        this.description = descripcion;
    }

    @JsonProperty("tipo")
    public void setTipo(MovementType tipo) {
        this.type = tipo;
    }

    @JsonProperty("categoria")
    public void setCategoria(String categoria) {
        this.category = categoria;
    }
}
