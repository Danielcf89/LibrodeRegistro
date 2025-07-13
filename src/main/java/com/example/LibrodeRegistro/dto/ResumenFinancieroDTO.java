package com.example.LibrodeRegistro.dto;

import java.math.BigDecimal;
import java.util.Map;
public class ResumenFinancieroDTO {
    private BigDecimal ingresosTotales;
    private BigDecimal gastosTotales;
    private BigDecimal balance;
    private Map<String, BigDecimal> detallePorCategoria;

    public ResumenFinancieroDTO(BigDecimal ingresosTotales, BigDecimal gastosTotales, Map<String, BigDecimal> detallePorCategoria) {
        this.ingresosTotales = ingresosTotales;
        this.gastosTotales = gastosTotales;
        this.balance = ingresosTotales.subtract(gastosTotales);
        this.detallePorCategoria = detallePorCategoria;
    }

    public BigDecimal getIngresosTotales() { return ingresosTotales; }
    public BigDecimal getGastosTotales() { return gastosTotales; }
    public BigDecimal getBalance() { return balance; }
    public Map<String, BigDecimal> getDetallePorCategoria() { return detallePorCategoria; }
}
