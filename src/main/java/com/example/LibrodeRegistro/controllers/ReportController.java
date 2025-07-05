package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.security.CustonUserDetails;
import com.example.LibrodeRegistro.service.MovementService;
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

    @GetMapping("/saldo")
    public ResponseEntity<Map<String, Object>> getBalanceReport(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio
    ) {
        Long userId = getCurrentUserId();

        // Si no se pasa mes/año, usar el actual
        LocalDate ahora = LocalDate.now();
        int mesConsulta = (mes != null) ? mes : ahora.getMonthValue();
        int anioConsulta = (anio != null) ? anio : ahora.getYear();

        // 1️⃣ Fecha del primer día del mes consultado
        LocalDate primerDiaMes = YearMonth.of(anioConsulta, mesConsulta).atDay(1);

        // 2️⃣ Totales filtrados por mes y usuario
        BigDecimal ingresosMes = movementService.getTotalByTypeInMonth(userId, MovementType.INGRESO, mesConsulta, anioConsulta);
        BigDecimal gastosMes = movementService.getTotalByTypeInMonth(userId, MovementType.GASTO, mesConsulta, anioConsulta);

        // 3️⃣ Saldo anterior acumulado antes del mes
        BigDecimal saldoAnterior = movementService.getTotalByTypeBeforeDate(userId, MovementType.INGRESO, primerDiaMes)
                .subtract(movementService.getTotalByTypeBeforeDate(userId, MovementType.GASTO, primerDiaMes));

        // 4️⃣ Saldo actual = saldo anterior + ingresos del mes - gastos del mes
        BigDecimal saldoActual = saldoAnterior.add(ingresosMes).subtract(gastosMes);

        // 5️⃣ Construcción de la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("saldoAnterior", saldoAnterior);
        response.put("ingresosMes", ingresosMes);
        response.put("gastosMes", gastosMes);
        response.put("saldoActual", saldoActual);
        response.put("mes", mesConsulta);
        response.put("año", anioConsulta); // Se escribe con 'ñ' porque es parte del JSON
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




