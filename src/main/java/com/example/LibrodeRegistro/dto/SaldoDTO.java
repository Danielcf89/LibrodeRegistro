package com.example.LibrodeRegistro.dto;

public class SaldoDTO {
    private long movimientosGenerales;
    private long totalAlcancias;
    private long saldoGlobal;

    public SaldoDTO(long movimientosGenerales, long totalAlcancias) {
        this.movimientosGenerales = movimientosGenerales;
        this.totalAlcancias = totalAlcancias;
        this.saldoGlobal = movimientosGenerales + totalAlcancias;
    }

    public long getMovimientosGenerales() {
        return movimientosGenerales;
    }

    public long getTotalAlcancias() {
        return totalAlcancias;
    }

    public long getSaldoGlobal() {
        return saldoGlobal;
    }
}

