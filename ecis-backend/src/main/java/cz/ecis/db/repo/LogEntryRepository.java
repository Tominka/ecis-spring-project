package cz.ecis.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.LogEntryEnt;

public interface LogEntryRepository extends JpaRepository<LogEntryEnt, Long> {

}
