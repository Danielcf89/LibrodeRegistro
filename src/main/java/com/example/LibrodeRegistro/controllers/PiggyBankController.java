package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.PiggyBankRequest;
import com.example.LibrodeRegistro.dto.PiggyBankResponse;
import com.example.LibrodeRegistro.dto.PiggyMovementResponse;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.entity.PiggyBank;
import com.example.LibrodeRegistro.exception.RetiroExcedeAhorroException;
import com.example.LibrodeRegistro.service.PiggyBankService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alcancia")
public class PiggyBankController {

    @Autowired
    private PiggyBankService piggyBankService;

    @PostMapping
    public ResponseEntity<PiggyBankResponse> crearAlcancia(@RequestBody PiggyBankRequest request) {
        return ResponseEntity.ok(piggyBankService.crearAlcancia(request));
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<PiggyBankResponse> depositarEnAlcancia(
            @PathVariable Long id,
            @RequestBody @Valid MovementRequest request) {
        PiggyBank alcanciaActualizada = piggyBankService.depositToPiggyBank(id, request);
        return ResponseEntity.ok(piggyBankService.mapToDto(alcanciaActualizada));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PiggyBankResponse> actualizarAlcancia(
            @PathVariable Long id,
            @RequestBody PiggyBankRequest request) {
        return ResponseEntity.ok(piggyBankService.actualizarAlcancia(id, request));
    }


    @GetMapping
    public ResponseEntity<List<PiggyBankResponse>> listarAlcancias() {
        return ResponseEntity.ok(piggyBankService.obtenerAlcanciasDelUsuario());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiggyBankResponse> consultarDetalle(@PathVariable Long id) {
        PiggyBank piggy = piggyBankService.getPiggyBankEntity(id); // nuevo método que retorna la entidad
        return ResponseEntity.ok(piggyBankService.mapToDtoWithMovements(piggy));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlcancia(@PathVariable Long id) {
        piggyBankService.eliminarAlcancia(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/retiro")
    public ResponseEntity<PiggyBankResponse> retirarDeAlcancia(
            @PathVariable Long id,
            @RequestBody MovementRequest request) {
        PiggyBankResponse response = piggyBankService.retirarDeAlcancia(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<PiggyMovementResponse>> filtrarMovimientos(
            @PathVariable Long id,
            @RequestParam(required = false) MovementType tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<PiggyMovementResponse> movimientos = piggyBankService.filtrarMovimientos(id, tipo, desde, hasta);
        return ResponseEntity.ok(movimientos);
    }



    @ExceptionHandler(RetiroExcedeAhorroException.class)
    public ResponseEntity<String> handleRetiroExcede(RetiroExcedeAhorroException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}

