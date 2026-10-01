package cz.ecis.db.repo;

import cz.ecis.core.repo.CampAwareRepository;
import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ParentEnt;

public interface ParentRepository extends VersionAwareRepository<ParentEnt, Long>, CampAwareRepository<ParentEnt, Long> {

}
