package cz.ecis.db.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.ChildEnt;
import cz.ecis.utils.CrudUtils;

public interface ChildRepository extends VersionAwareRepository<ChildEnt, Long>, QuerydslPredicateExecutor<ChildEnt> {

    List<ChildEnt> findAll(Predicate predicate);

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

    Optional<ChildEnt> findByNameAndSurnameAndBirthdateAndAddress(String name, String surname, LocalDate birthdate, String address);
}
