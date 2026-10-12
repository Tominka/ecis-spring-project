package cz.ecis.db.repo;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ApplicationDocumentEnt;
import cz.ecis.utils.CrudUtils;

public interface ApplicationDocumentRepository extends VersionAwareRepository<ApplicationDocumentEnt, UUID> {

    Optional<ApplicationDocumentEnt> findByIdAndApplicationId(UUID id, Long applicationId);

    default ApplicationDocumentEnt findByIdAndApplicationIdAndCampId(UUID id, Long applicationId, Long campId) {
        ApplicationDocumentEnt ent = this.findByIdAndApplicationId(id, applicationId)
        .orElseThrow(() -> new EntityNotExistsException("Application with given id not found",
            Map.of("id", id.toString(), "documentId", id.toString())
        ));

        CrudUtils.checkEntityCamp(ent.getApplication(), campId);

        return ent;
    }

}
