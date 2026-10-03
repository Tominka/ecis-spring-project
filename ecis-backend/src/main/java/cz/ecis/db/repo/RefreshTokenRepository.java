package cz.ecis.db.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.RefreshTokenEnt;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEnt, Long> {

    Optional<RefreshTokenEnt> findBySessionIdAndRevokedAtIsNull(String sessionId);
}
