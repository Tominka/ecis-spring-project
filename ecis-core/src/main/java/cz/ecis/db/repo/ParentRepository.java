package cz.ecis.db.repo;

import java.util.List;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ParentEnt;
import cz.ecis.utils.CrudUtils;

public interface ParentRepository extends VersionAwareRepository<ParentEnt, Long> {

    List<ParentEnt> findAllByApplicationsCampId(Long campId);

    ParentEnt findByIdAndApplicationsCampId(Long id, Long campId);

    boolean existsByIdAndApplicationsCampId(Long id, Long campId);

    default ParentEnt findByIdAndCampId(Long id, Long campId) {
        ParentEnt ent = this.findByIdAndApplicationsCampId(id, campId);
        if (ent == null) {
            CrudUtils.handleEntityNotFoundInCamp(campId, getClass());
        }

        return ent;
    }
}
