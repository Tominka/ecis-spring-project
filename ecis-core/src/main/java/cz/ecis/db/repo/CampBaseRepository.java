package cz.ecis.db.repo;

import org.springframework.data.repository.NoRepositoryBean;

import cz.ecis.core.repo.VersionAwareRepository;
import cz.ecis.db.ent.CampEnt;

@NoRepositoryBean
public interface CampBaseRepository extends VersionAwareRepository<CampEnt, Long> {

    boolean existsById(Long campId);

}
