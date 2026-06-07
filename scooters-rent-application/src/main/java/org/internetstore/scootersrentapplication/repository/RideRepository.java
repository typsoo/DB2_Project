package org.internetstore.scootersrentapplication.repository;

import org.internetstore.scootersrentapplication.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Integer> {
}
