package com.example.LibrodeRegistro.repository;

import com.example.LibrodeRegistro.entity.Movement;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    // Consultas básicas
    List<Movement> findByUser(User user);
    List<Movement> findByUserAndType(User user, MovementType type);
    List<Movement> findByUserAndCategory(User user, String category);
    List<Movement> findByUserAndTypeAndCategory(User user, MovementType type, String category);

    // Consulta personalizada para suma
    @Query("SELECT SUM(m.amount) FROM Movement m WHERE m.user = :user AND m.type = :type")
    Optional<BigDecimal> sumAmountByUserAndType(
            @Param("user") User user,
            @Param("type") MovementType type
    );
}