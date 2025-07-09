package com.example.LibrodeRegistro.service;

import com.example.LibrodeRegistro.dto.MovementRequest;
import com.example.LibrodeRegistro.dto.PiggyBankRequest;
import com.example.LibrodeRegistro.dto.PiggyBankResponse;
import com.example.LibrodeRegistro.dto.PiggyMovementDto;
import com.example.LibrodeRegistro.dto.PiggyMovementResponse;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.entity.PiggyBank;
import com.example.LibrodeRegistro.entity.PiggyMovement;
import com.example.LibrodeRegistro.entity.User;
import com.example.LibrodeRegistro.exception.RetiroExcedeAhorroException;
import com.example.LibrodeRegistro.repository.PiggyBankRepository;
import com.example.LibrodeRegistro.repository.PiggyMovementRepository;
import com.example.LibrodeRegistro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PiggyBankService {

    @Autowired
    private PiggyBankRepository piggyBankRepository;

    @Autowired
    private PiggyMovementRepository piggyMovementRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
    }

    private PiggyBank validateOwnership(Long id) {
        PiggyBank piggy = piggyBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alcancía no encontrada"));
        if (!piggy.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("No tienes permiso para acceder a esta alcancía");
        }
        return piggy;
    }

    public PiggyBankResponse mapToDto(PiggyBank piggyBank) {
        PiggyBankResponse dto = new PiggyBankResponse();
        dto.setId(piggyBank.getId());
        dto.setNombre(piggyBank.getNombre());
        dto.setMeta(piggyBank.getMeta());
        dto.setFechaLimite(piggyBank.getFechaLimite());
        dto.setAhorroLibre(piggyBank.isAhorroLibre());
        if (!piggyBank.isAhorroLibre()) {
            BigDecimal restante = piggyBank.getMontoFaltante();
            long semanas = piggyBank.getSemanasRestantes();

            BigDecimal recomendado = semanas > 0
                    ? restante.divide(BigDecimal.valueOf(semanas), 2, BigDecimal.ROUND_HALF_UP)
                    : restante;

            dto.setMontoRecomendadoSemanal(recomendado.max(BigDecimal.ZERO));
        } else {
            dto.setMontoRecomendadoSemanal(BigDecimal.ZERO);
        }
        dto.setTotalAhorrado(piggyBank.getTotalAhorrado());
        dto.setMontoFaltante(piggyBank.getMontoFaltante());
        dto.setSemanasRestantes(piggyBank.getSemanasRestantes());
        return dto;
    }

    public PiggyBankResponse mapToDtoWithMovements(PiggyBank piggyBank) {
        PiggyBankResponse dto = mapToDto(piggyBank);
        List<PiggyMovementResponse> movimientos = piggyBank.getMovimientos().stream().map(mov -> {
            PiggyMovementResponse movResp = new PiggyMovementResponse();
            movResp.setId(mov.getId());
            movResp.setAmount(mov.getAmount());
            movResp.setDate(mov.getDate());
            movResp.setDescription(mov.getDescription());
            movResp.setType(mov.getType());
            movResp.setCategory(mov.getCategory());
            return movResp;
        }).collect(Collectors.toList());
        dto.setMovimientos(movimientos);
        return dto;
    }


    public PiggyBankResponse crearAlcancia(PiggyBankRequest request) {
        PiggyBank piggyBank = new PiggyBank();
        piggyBank.setNombre(request.getNombre());
        piggyBank.setMeta(request.getMeta());
        piggyBank.setFechaLimite(request.getFechaLimite());
        piggyBank.setAhorroLibre(request.isAhorroLibre());
        piggyBank.setUser(getCurrentUser());

        PiggyBank guardada = piggyBankRepository.save(piggyBank);
        return mapToDto(guardada);
    }

    public PiggyBankResponse actualizarAlcancia(Long id, PiggyBankRequest request) {
        PiggyBank piggy = validateOwnership(id);

        piggy.setNombre(request.getNombre());
        piggy.setFechaLimite(request.getFechaLimite());
        piggy.setAhorroLibre(request.isAhorroLibre());
        piggy.setMeta(request.getMeta());

        if (!request.isAhorroLibre()) {
            piggy.setMontoRecomendadoSemanal(request.getMontoRecomendadoSemanal());
        } else {
            piggy.setMontoRecomendadoSemanal(BigDecimal.ZERO);
        }

        PiggyBank actualizado = piggyBankRepository.save(piggy);
        return mapToDto(actualizado);
    }

    public List<PiggyBankResponse> obtenerAlcanciasDelUsuario() {
        return piggyBankRepository.findByUser(getCurrentUser())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PiggyBankResponse obtenerDetalleAlcancia(Long id) {
        return mapToDtoWithMovements(validateOwnership(id));
    }


    public void eliminarAlcancia(Long id) {
        piggyBankRepository.delete(validateOwnership(id));
    }

    public PiggyBankResponse retirarDeAlcancia(Long id, MovementRequest request) {
        PiggyBank piggy = validateOwnership(id);

        BigDecimal totalAhorrado = piggy.getTotalAhorrado();
        if (request.getAmount().compareTo(totalAhorrado) > 0) {
            throw new RetiroExcedeAhorroException("No puedes retirar más de lo que has ahorrado. Total disponible: " + totalAhorrado);
        }

        PiggyMovement retiro = new PiggyMovement();
        retiro.setAmount(request.getAmount().negate());
        retiro.setDate(request.getDate());
        retiro.setDescription(request.getDescription());
        retiro.setType(request.getType());
        retiro.setCategory(request.getCategory());
        retiro.setPiggyBank(piggy);
        piggyMovementRepository.save(retiro);

        piggy.getMovimientos().add(retiro);

        // ✅ Usar el DTO sin movimientos
        return mapToDto(piggyBankRepository.save(piggy));
    }


    public PiggyBank depositToPiggyBank(Long piggyBankId, MovementRequest request) {
        PiggyBank piggyBank = validateOwnership(piggyBankId);

        PiggyMovement movement = new PiggyMovement();
        movement.setAmount(request.getAmount());
        movement.setDate(request.getDate());
        movement.setDescription(request.getDescription());
        movement.setType(request.getType());
        movement.setCategory(request.getCategory());
        movement.setPiggyBank(piggyBank);
        piggyMovementRepository.save(movement);

        piggyBank.getMovimientos().add(movement);
        return piggyBankRepository.save(piggyBank);
    }

    public PiggyBankResponse retirarMonto(Long id, BigDecimal monto) {
        PiggyBank piggy = validateOwnership(id);

        PiggyMovement retiro = new PiggyMovement();
        retiro.setAmount(monto.negate());
        retiro.setDate(LocalDate.now());
        retiro.setDescription("Retiro de la alcancía");
        retiro.setPiggyBank(piggy);
        piggyMovementRepository.save(retiro);

        piggy.getMovimientos().add(retiro);
        return mapToDto(piggyBankRepository.save(piggy));
    }

    public PiggyBank getPiggyBankEntity(Long id) {
        return validateOwnership(id);
    }

    public List<PiggyMovementResponse> filtrarMovimientos(Long id, MovementType tipo, LocalDate desde, LocalDate hasta) {
        PiggyBank piggy = validateOwnership(id);

        return piggy.getMovimientos().stream()
                .filter(mov -> tipo == null || mov.getType() == tipo)
                .filter(mov -> desde == null || !mov.getDate().isBefore(desde))
                .filter(mov -> hasta == null || !mov.getDate().isAfter(hasta))
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

}


