package cz.ecis.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.UserEnt;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEnt, Long> {

    Optional<UserEnt> findByUsername(String username);

    Optional<UserEnt> findByEmailIgnoreCase(String email);
}
