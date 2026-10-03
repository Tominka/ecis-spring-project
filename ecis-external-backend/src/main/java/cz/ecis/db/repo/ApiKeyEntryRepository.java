package cz.ecis.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.ApiKeyEntryEnt;

public interface ApiKeyEntryRepository extends JpaRepository<ApiKeyEntryEnt, Long> {
    
}
