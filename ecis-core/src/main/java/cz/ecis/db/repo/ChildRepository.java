package cz.ecis.db.repo;

import java.util.List;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ChildEnt;
import cz.ecis.utils.CrudUtils;

public interface ChildRepository extends VersionAwareRepository<ChildEnt, Long> {

    List<ChildEnt> findAllByApplicationsCampId(Long campId);

    ChildEnt findByIdAndApplicationsCampId(Long id, Long campId);

    boolean existsByIdAndApplicationsCampId(Long id, Long campId);

    default ChildEnt findByIdAndCampId(Long id, Long campId) {
        ChildEnt ent = this.findByIdAndApplicationsCampId(id, campId);
        if (ent == null) {
            CrudUtils.handleEntityNotFoundInCamp(campId, getClass());
        }

        return ent;
    }
}
