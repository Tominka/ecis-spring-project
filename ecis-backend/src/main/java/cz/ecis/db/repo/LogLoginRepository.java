package cz.ecis.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.LogLoginEnt;

public interface LogLoginRepository extends JpaRepository<LogLoginEnt, Long> {

}
