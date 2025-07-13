package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.security.CustonUserDetails;
import com.example.LibrodeRegistro.service.MovementService;
import com.example.LibrodeRegistro.service.PiggyBankService;
import com.example.LibrodeRegistro.entity.MovementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

        import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportController {

    @Autowired
    private MovementService movementService;

    @Autowired
    private PiggyBankService piggyBankService;

    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> getBalanceReport(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio
    ) {
        Long userId = getCurrentUserId();

        LocalDate ahora = LocalDate.now();
        int mesConsulta = (mes != null) ? mes : ahora.getMonthValue();
        int anioConsulta = (anio != null) ? anio : ahora.getYear();

        LocalDate primerDiaMes = YearMonth.of(anioConsulta, mesConsulta).atDay(1);

        BigDecimal ingresosMes = movementService.getTotalByTypeInMonth(userId, MovementType.INGRESO, mesConsulta, anioConsulta);
        BigDecimal gastosMes = movementService.getTotalByTypeInMonth(userId, MovementType.GASTO, mesConsulta, anioConsulta);
        BigDecimal saldoAnterior = movementService.getTotalByTypeBeforeDate(userId, MovementType.INGRESO, primerDiaMes)
                .subtract(movementService.getTotalByTypeBeforeDate(userId, MovementType.GASTO, primerDiaMes));
        BigDecimal saldoActual = saldoAnterior.add(ingresosMes).subtract(gastosMes);

        BigDecimal totalAlcancias = piggyBankService.obtenerTotalAlcancias();

        Map<String, Object> response = new HashMap<>();
        response.put("saldoAnterior", saldoAnterior);
        response.put("ingresosMes", ingresosMes);
        response.put("gastosMes", gastosMes);
        response.put("saldoActual", saldoActual);
        response.put("totalAlcancias", totalAlcancias);
        response.put("saldoGlobal", saldoActual.add(totalAlcancias));
        response.put("mes", mesConsulta);
        response.put("año", anioConsulta);
        response.put("moneda", "COP");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumenFinanciero(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio
    ) {
        Long userId = getCurrentUserId();

        LocalDate ahora = LocalDate.now();
        int mesConsulta = (mes != null) ? mes : ahora.getMonthValue();
        int anioConsulta = (anio != null) ? anio : ahora.getYear();

        Map<String, BigDecimal> ingresosPorCategoria = movementService.getTotalByCategoryInMonth(userId, MovementType.INGRESO, mesConsulta, anioConsulta);
        Map<String, BigDecimal> gastosPorCategoria = movementService.getTotalByCategoryInMonth(userId, MovementType.GASTO, mesConsulta, anioConsulta);

        BigDecimal ingresosTotales = ingresosPorCategoria.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gastosTotales = gastosPorCategoria.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = ingresosTotales.subtract(gastosTotales);

        Map<String, Object> response = new HashMap<>();
        response.put("ingresosTotales", ingresosTotales);
        response.put("gastosTotales", gastosTotales);
        response.put("balance", balance);
        response.put("detalleIngresos", ingresosPorCategoria);
        response.put("detalleGastos", gastosPorCategoria);
        response.put("mes", mesConsulta);
        response.put("año", anioConsulta);
        response.put("moneda", "COP");

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustonUserDetails) {
            return ((CustonUserDetails) principal).getUser().getId();
        }
        throw new RuntimeException("Usuario no autenticado o mal configurado");
    }
}




