package cz.ecis.db.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.LoginAttemptEnt;

public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEnt, Long> {

    Optional<LoginAttemptEnt> findFirstByIpAddressAndHistory(String ipAddress, boolean isHistory);
}
