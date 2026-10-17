package cz.ecis.db.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.db.ent.ApplicationOverviewVieEnt;
import cz.ecis.utils.CrudUtils;

public interface ApplicationOverviewVieRepository extends JpaRepository<ApplicationOverviewVieEnt, Long>, QuerydslPredicateExecutor<ApplicationOverviewVieEnt> {

    List<ApplicationOverviewVieEnt> findAll(Predicate predicate);

    default ApplicationOverviewVieEnt findByIdAndCampId(Long id, Long campId) {
        ApplicationOverviewVieEnt ent = this.findById(id)
        .orElseThrow(() -> new EntityNotExistsException("Record with given id not found",
            Map.of("id", id)
        ));

        CrudUtils.checkEntityCamp(ent, campId);

        return ent;
    }

}