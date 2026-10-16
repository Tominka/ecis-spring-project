package cz.ecis.db.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import cz.ecis.db.ent.PasswordResetEnt;
import jakarta.persistence.LockModeType;

public interface PasswordResetRepository extends JpaRepository<PasswordResetEnt, UUID> {

    Optional<PasswordResetEnt> findByTokenHash(String sessionId);

    Optional<PasswordResetEnt> findFirstByUserIdOrderByGeneratedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetEnt> findFirstByUserEmailAndTokenHash(String email, String tokenHash);
}
