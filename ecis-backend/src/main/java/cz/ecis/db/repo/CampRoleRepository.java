package cz.ecis.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ecis.db.ent.CampRoleEnt;

public interface CampRoleRepository extends JpaRepository<CampRoleEnt, Long>  {

    List<CampRoleEnt> findAllByRoleGlobalTrueAndRoleEnabledTrueAndUserId(Long userId);
    List<CampRoleEnt> findAllByRoleGlobalFalseAndRoleEnabledTrueAndUserIdAndCampId(Long userId, Long campId);
}
