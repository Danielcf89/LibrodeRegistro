// MovementController.java
package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.MovementResponse;
import com.example.LibrodeRegistro.dto.PiggyMovementResponse;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.entity.User;
import com.example.LibrodeRegistro.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos")
public class MovementController {

    @Autowired
    private MovementService movementService;

    @GetMapping
    public ResponseEntity<List<PiggyMovementResponse>> filtrarMovimientosGenerales(
            @RequestParam(required = false) MovementType tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<PiggyMovementResponse> resultados = movementService.filtrarMovimientos(tipo, desde, hasta);
        return ResponseEntity.ok(resultados);
    }


    @PostMapping
    public ResponseEntity<MovementResponse> createMovement(@RequestBody MovementRequest request) {
        MovementResponse response = movementService.createMovement(request);
        return ResponseEntity.ok(response);
    }
    // MovementController.java
    @PutMapping("/{id}")
    public ResponseEntity<MovementResponse> updateMovement(
            @PathVariable Long id,
            @RequestBody MovementRequest request) {

        MovementResponse response = movementService.updateMovement(id, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, BigDecimal>> obtenerResumen(
            @RequestParam Integer desdeMes,
            @RequestParam Integer hastaMes,
            @RequestParam Integer anio
    ) {
        User currentUser = movementService.getCurrentUser();
        Long userId = currentUser.getId();

        BigDecimal ingresos = movementService.getTotalByTypeBetweenMonths(userId, MovementType.INGRESO, desdeMes, hastaMes, anio);
        BigDecimal gastos = movementService.getTotalByTypeBetweenMonths(userId, MovementType.GASTO, desdeMes, hastaMes, anio);

        Map<String, BigDecimal> resumen = new LinkedHashMap<>();
        resumen.put("ingresos", ingresos != null ? ingresos : BigDecimal.ZERO);
        resumen.put("gastos", gastos != null ? gastos : BigDecimal.ZERO);
        resumen.put("balance", ingresos.subtract(gastos));

        return ResponseEntity.ok(resumen);
    }


}