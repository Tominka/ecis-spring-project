package cz.ecis.db.repo;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.repo.EcisLookupRepository;
import cz.ecis.db.ent.LkpDocumentTypeEnt;
import cz.ecis.utils.CrudUtils;

public interface LkpDocumentTypeRepository extends EcisLookupRepository<LkpDocumentTypeEnt, UUID> {
    Optional<LkpDocumentTypeEnt> findByExtensionIgnoreCase(String extension);

    default LkpDocumentTypeEnt findByExtensionIgnoreCaseValid(String extension) {
        LkpDocumentTypeEnt ent = this.findByExtensionIgnoreCase(extension).orElseThrow(
            () -> new EntityNotExistsException("Record not found", Map.of("extension", extension))
        );

        CrudUtils.checkLookupValidity(ent);

        return ent;
    }
}
