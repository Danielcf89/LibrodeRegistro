// MovementController.java
package com.example.LibrodeRegistro.controllers;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.MovementResponse;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovementController {

    @Autowired
    private MovementService movementService;

    @GetMapping
    public List<MovementResponse> getUserMovements(
            @RequestParam(required = false) MovementType type,
            @RequestParam(required = false) String category) {

        return movementService.getUserMovements(type, category);
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
}