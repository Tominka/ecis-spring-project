package cz.ecis.db.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ParentEnt;
import cz.ecis.utils.CrudUtils;

public interface ParentRepository extends VersionAwareRepository<ParentEnt, Long> {

    @Query("""
        SELECT p
        FROM ParentEnt p
        JOIN p.applications ap
        JOIN ap.application a
        WHERE a.camp.id = :campId
    """)
    List<ParentEnt> findAllByCampId(Long campId);

    @Query("""
        SELECT p
        FROM ParentEnt p
        JOIN p.applications ap
        JOIN ap.application a
        WHERE p.id = :id
        AND a.camp.id = :campId
    """)
    ParentEnt findByIdAndCampIdNoCheck(Long id, Long campId);

    @Query("""
        SELECT COUNT(p) > 0
        FROM ParentEnt p
        JOIN p.applications ap
        JOIN ap.application a
        WHERE p.id = :id
        AND a.camp.id = :campId
    """)
    boolean existsByIdAndCampId(Long id, Long campId);

    default ParentEnt findByIdAndCampId(Long id, Long campId) {
        ParentEnt ent = this.findByIdAndCampIdNoCheck(id, campId);
        if (ent == null) {
            CrudUtils.handleEntityNotFoundInCamp(campId, getClass());
        }

        return ent;
    }

    Optional<ParentEnt> findByNameAndSurnameAndAddress(String name, String surname, String address);

}
