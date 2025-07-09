// MovementRequest.java
package com.example.LibrodeRegistro.dto;

import com.example.LibrodeRegistro.entity.MovementType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovementRequest {

    @NotNull(message = "El monto es obligatorio.")
    private BigDecimal amount;

    @NotNull(message = "La fecha es obligatoria.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "El tipo es obligatorio (INGRESO o GASTO).")
    private MovementType type;

    @NotBlank(message = "La categoría no puede estar vacía.")
    private String category;

    // Alias en español para recibir JSON con nombres en español
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
