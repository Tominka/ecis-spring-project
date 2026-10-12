package cz.ecis.db.repo;

import java.util.Map;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ApplicationEnt;
import cz.ecis.utils.CrudUtils;
import cz.ecis.core.exception.EntityNotExistsException;

public interface ApplicationRepository extends VersionAwareRepository<ApplicationEnt, Long> {

    default ApplicationEnt findByIdAndCampId(Long id, Long campId) {
        ApplicationEnt ent = this.findById(id)
        .orElseThrow(() -> new EntityNotExistsException("Record with given id not found",
            Map.of("id", id)
        ));

        CrudUtils.checkEntityCamp(ent, campId);

        return ent;
    }

}
