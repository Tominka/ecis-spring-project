package cz.ecis.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.RoleEnt;

public interface RoleRepository extends JpaRepository<RoleEnt, Long> {

    List<RoleEnt> findAllByEnabledTrue();

    List<RoleEnt> findAllByGlobalFalseAndEnabledTrue();

}