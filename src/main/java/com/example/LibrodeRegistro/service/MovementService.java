// MovementService.java
package com.example.LibrodeRegistro.service;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.MovementResponse;
import com.example.LibrodeRegistro.entity.*;
import com.example.LibrodeRegistro.repository.MovementRepository;
import com.example.LibrodeRegistro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService {

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private UserRepository userRepository;

    // Obtiene movimientos con filtros
    public List<MovementResponse> getUserMovements(MovementType type, String category) {
        User currentUser = getCurrentUser();
        List<Movement> movements;

        if (type != null && category != null) {
            movements = movementRepository.findByUserAndTypeAndCategory(currentUser, type, category);
        } else if (type != null) {
            movements = movementRepository.findByUserAndType(currentUser, type);
        } else if (category != null) {
            movements = movementRepository.findByUserAndCategory(currentUser, category);
        } else {
            movements = movementRepository.findByUser(currentUser);
        }

        return movements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Calcula el balance actual
    public BigDecimal calculateCurrentBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BigDecimal ingresos = movementRepository.sumAmountByUserAndType(user, MovementType.INGRESO)
                .orElse(BigDecimal.ZERO);

        BigDecimal gastos = movementRepository.sumAmountByUserAndType(user, MovementType.GASTO)
                .orElse(BigDecimal.ZERO);

        return ingresos.subtract(gastos);
    }

    // Crea un nuevo movimiento
    @Transactional
    public MovementResponse createMovement(MovementRequest request) {
        User currentUser = getCurrentUser();

        // Validación básica del request
        validateMovementRequest(request);

        Movement movement = new Movement();
        movement.setAmount(request.getAmount());
        movement.setDate(request.getDate());
        movement.setDescription(request.getDescription());
        movement.setType(request.getType());
        movement.setCategory(request.getCategory());
        movement.setUser(currentUser);

        Movement savedMovement = movementRepository.save(movement);
        return convertToDto(savedMovement);
    }

    // Convierte entidad a DTO
    private MovementResponse convertToDto(Movement movement) {
        MovementResponse response = new MovementResponse();
        response.setId(movement.getId());
        response.setAmount(movement.getAmount());
        response.setDate(movement.getDate());
        response.setDescription(movement.getDescription());
        response.setType(movement.getType());
        response.setCategory(movement.getCategory());
        return response;
    }

    // Obtiene usuario autenticado
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }

    // Validación básica de los datos del movimiento
    private void validateMovementRequest(MovementRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser un valor positivo");
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es requerido");
        }

        if (request.getDate() == null) {
            throw new IllegalArgumentException("La fecha es requerida");
        }
    }
}