package org.internetstore.scootersrentapplication.repository;

import jakarta.persistence.LockModeType;
import org.internetstore.scootersrentapplication.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByUserId(Integer userId);

    // Physical row lock on the wallet in the database.
    // Prevents concurrent balance modifications until the ride billing is completed.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithLock(Integer userId);
}