package cz.ecis.db.repo;

import cz.ecis.core.repo.CampAwareRepository;
import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ChildEnt;

public interface ChildRepository extends VersionAwareRepository<ChildEnt, Long>, CampAwareRepository<ChildEnt, Long> {

}
