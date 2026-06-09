package org.internetstore.scootersrentapplication.repository;

import jakarta.persistence.LockModeType;
import org.internetstore.scootersrentapplication.entity.Scooter;
import org.internetstore.scootersrentapplication.entity.enums.ScooterStatus;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Integer> {

    List<Scooter> findByStatus(ScooterStatus status);

    // Wymuszamy blokadę na poziomie bazy danych (SELECT ... FOR UPDATE).
    // Zapobiega to sytuacji, gdzie 2 osoby jednocześnie wypożyczają tę samą hulajnogę.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Scooter s WHERE s.id = :id")
    Optional<Scooter> findByIdWithLock(@Param("id") Integer id);


    // We are looking for scooters in a certain area.
    @Query("SELECT s FROM Scooter s WHERE s.status = :status AND within(s.location, :bbox) = true")
    List<Scooter> findAvailableInArea(@Param("status") ScooterStatus status, @Param("bbox") Polygon bbox);

}
