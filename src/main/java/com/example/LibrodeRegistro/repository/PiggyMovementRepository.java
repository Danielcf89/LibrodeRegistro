package com.example.LibrodeRegistro.repository;

import com.example.LibrodeRegistro.entity.PiggyMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PiggyMovementRepository extends JpaRepository<PiggyMovement, Long> {
    List<PiggyMovement> findByPiggyBankId(Long piggyBankId);
}

