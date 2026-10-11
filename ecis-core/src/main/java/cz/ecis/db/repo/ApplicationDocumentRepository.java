package cz.ecis.db.repo;

import java.util.UUID;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ApplicationDocumentEnt;

public interface ApplicationDocumentRepository extends VersionAwareRepository<ApplicationDocumentEnt, UUID> {

}
