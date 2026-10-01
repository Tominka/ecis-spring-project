package cz.ecis.db.repo;

import java.util.List;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ChildEnt;

public interface ChildRepository extends VersionAwareRepository<ChildEnt, Long> {

    List<ChildEnt> findAllByCampId(Long campId);
}
