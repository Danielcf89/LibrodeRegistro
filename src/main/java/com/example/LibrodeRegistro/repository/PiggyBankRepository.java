package com.example.LibrodeRegistro.repository;

import com.example.LibrodeRegistro.entity.PiggyBank;
import com.example.LibrodeRegistro.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PiggyBankRepository extends JpaRepository<PiggyBank, Long> {

    @EntityGraph(attributePaths = {"movimientos"})
    List<PiggyBank> findByUser(User user);

    @EntityGraph(attributePaths = {"movimientos"})
    Optional<PiggyBank> findById(Long id); // <- Este es el nuevo método

    @Query("SELECT p FROM PiggyBank p LEFT JOIN FETCH p.movimientos WHERE p.id = :id")
    Optional<PiggyBank> findByIdWithMovimientos(@Param("id") Long id);


}

