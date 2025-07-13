package com.example.LibrodeRegistro.repository;

import com.example.LibrodeRegistro.entity.Movement;
import com.example.LibrodeRegistro.entity.MovementType;
import com.example.LibrodeRegistro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    // Consultas básicas
    List<Movement> findByUser(User user);
    List<Movement> findByUserAndType(User user, MovementType type);
    List<Movement> findByUserAndCategory(User user, String category);
    List<Movement> findByUserAndTypeAndCategory(User user, MovementType type, String category);


    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM Movement m WHERE m.user.id = :userId AND m.type = :type AND m.date < :fecha")
    BigDecimal getTotalByTypeBeforeDate(
            @Param("userId") Long userId,
            @Param("type") MovementType type,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
    SELECT COALESCE(SUM(m.amount), 0)
    FROM Movement m
    WHERE m.user.id = :userId
      AND m.type = :type
      AND FUNCTION('MONTH', m.date) = :mes
      AND FUNCTION('YEAR', m.date) = :anio
""")
    BigDecimal getTotalByTypeInMonth(
            @Param("userId") Long userId,
            @Param("type") MovementType type,
            @Param("mes") int mes,
            @Param("anio") int anio
    );

    @Query("SELECT SUM(m.amount) FROM Movement m WHERE m.user = :user AND m.type = :type")
    Optional<BigDecimal> sumAmountByUserAndType(@Param("user") User user, @Param("type") MovementType type);


    @Query("SELECT SUM(m.amount) FROM Movement m WHERE m.user.id = :userId AND m.type = :type AND MONTH(m.date) BETWEEN :mesInicio AND :mesFin AND YEAR(m.date) = :anio")
    BigDecimal getTotalByTypeBetweenMonths(@Param("userId") Long userId,
                                           @Param("type") MovementType type,
                                           @Param("mesInicio") int mesInicio,
                                           @Param("mesFin") int mesFin,
                                           @Param("anio") int anio);


    @Query("SELECT SUM(m.amount) FROM Movement m WHERE m.type = 'INGRESO'")
    BigDecimal sumarIngresos();

    @Query("SELECT SUM(m.amount) FROM Movement m WHERE m.type = 'GASTO'")
    BigDecimal sumarGastos();

    @Query("SELECT m.category, SUM(m.amount) FROM Movement m GROUP BY m.category")
    List<Object[]> sumarPorCategoria();



    @Query("SELECT m.category, SUM(m.amount) FROM Movement m " +
            "WHERE m.user.id = :userId AND m.type = :tipo " +
            "AND MONTH(m.date) = :mes AND YEAR(m.date) = :anio " +
            "GROUP BY m.category")
    List<Object[]> sumarPorCategoriaYMes(@Param("userId") Long userId,
                                         @Param("tipo") MovementType tipo,  // ✔️ CORRECTO
                                         @Param("mes") int mes,
                                         @Param("anio") int anio);





}