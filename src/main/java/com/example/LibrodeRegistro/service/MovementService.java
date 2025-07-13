package com.example.LibrodeRegistro.service;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.MovementResponse;
import com.example.LibrodeRegistro.dto.PiggyMovementResponse;
import com.example.LibrodeRegistro.dto.ResumenFinancieroDTO;
import com.example.LibrodeRegistro.entity.*;
import com.example.LibrodeRegistro.repository.MovementRepository;
import com.example.LibrodeRegistro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public BigDecimal getTotalByTypeBeforeDate(Long userId, MovementType type, LocalDate fechaCorte) {
        return movementRepository.getTotalByTypeBeforeDate(userId, type, fechaCorte);
    }

    public BigDecimal getTotalByTypeBetweenMonths(Long userId, MovementType type, int mesInicio, int mesFin, int anio) {
        return movementRepository.getTotalByTypeBetweenMonths(userId, type, mesInicio, mesFin, anio);
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

    // Actualiza un movimiento existente
    @Transactional
    public MovementResponse updateMovement(Long id, MovementRequest request) {
        User currentUser = getCurrentUser();

        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Verificar que el movimiento pertenezca al usuario
        if (!movement.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este movimiento");
        }

        // Validación básica de los campos actualizados
        validateUpdateMovementRequest(request);

        // Actualizar campos
        if (request.getAmount() != null) {
            movement.setAmount(request.getAmount());
        }
        if (request.getDate() != null) {
            movement.setDate(request.getDate());
        }
        if (request.getDescription() != null) {
            movement.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            movement.setType(request.getType());
        }
        if (request.getCategory() != null) {
            movement.setCategory(request.getCategory());
        }

        Movement updatedMovement = movementRepository.save(movement);
        return convertToDto(updatedMovement);
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
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }

    // Validación básica de los datos del movimiento para creación
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

    // Validación básica para actualización
    private void validateUpdateMovementRequest(MovementRequest request) {
        // Si se proporciona amount, debe ser positivo
        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser un valor positivo");
        }
    }

    public BigDecimal getTotalIngresos(Long userId, Integer mes, Integer anio) {
        return movementRepository.getTotalByTypeInMonth(userId, MovementType.INGRESO, mes, anio);
    }

    public BigDecimal getTotalGastos(Long userId, Integer mes, Integer anio) {
        return movementRepository.getTotalByTypeInMonth(userId, MovementType.GASTO, mes, anio);
    }
    public List<PiggyMovementResponse> filtrarMovimientos(MovementType tipo, LocalDate desde, LocalDate hasta) {
        List<Movement> movimientos = movementRepository.findAll();

        return movimientos.stream()
                .filter(m -> tipo == null || m.getType() == tipo)
                .filter(m -> desde == null || !m.getDate().isBefore(desde))
                .filter(m -> hasta == null || !m.getDate().isAfter(hasta))
                .map(mov -> {
                    PiggyMovementResponse dto = new PiggyMovementResponse();
                    dto.setId(mov.getId());
                    dto.setAmount(mov.getAmount());
                    dto.setDate(mov.getDate());
                    dto.setDescription(mov.getDescription());
                    dto.setType(mov.getType());
                    dto.setCategory(mov.getCategory());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public BigDecimal getTotalByTypeInMonth(Long userId, MovementType type, int mes, int anio) {
        return movementRepository.getTotalByTypeInMonth(userId, type, mes, anio);
    }

    public ResumenFinancieroDTO obtenerResumenFinanciero() {
        BigDecimal ingresos = Optional.ofNullable(movementRepository.sumarIngresos()).orElse(BigDecimal.ZERO);
        BigDecimal gastos = Optional.ofNullable(movementRepository.sumarGastos()).orElse(BigDecimal.ZERO);


        Map<String, BigDecimal> detalle = new LinkedHashMap<>();
        List<Object[]> resultados = movementRepository.sumarPorCategoria();
        for (Object[] fila : resultados) {
            String categoria = (String) fila[0];
            BigDecimal monto = (BigDecimal) fila[1];
            detalle.put(categoria, monto);
        }

        return new ResumenFinancieroDTO(ingresos, gastos, detalle);

    }
    public Map<String, BigDecimal> getTotalByCategoryInMonth(Long userId, MovementType tipo, int mes, int anio) {
        List<Object[]> resultados = movementRepository.sumarPorCategoriaYMes(userId, tipo, mes, anio);
        Map<String, BigDecimal> mapa = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            String categoria = (String) fila[0];
            BigDecimal monto = (BigDecimal) fila[1];
            mapa.put(categoria, monto);
        }
        return mapa;
    }




}