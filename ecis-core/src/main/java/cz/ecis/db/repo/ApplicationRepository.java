package cz.ecis.db.repo;

import java.util.Optional;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ApplicationEnt;

public interface ApplicationRepository extends VersionAwareRepository<ApplicationEnt, Long> {

    Optional<ApplicationEnt> findByIdAndCampId(Long id, Long campId);

}
