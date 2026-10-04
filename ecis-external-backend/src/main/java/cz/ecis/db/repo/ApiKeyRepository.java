package cz.ecis.db.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.ApiKeyEnt;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEnt, Long> {
    
    Optional<ApiKeyEnt> findByKeyHash(String keyHash);

    Optional<ApiKeyEnt> findByName(String name);
}
