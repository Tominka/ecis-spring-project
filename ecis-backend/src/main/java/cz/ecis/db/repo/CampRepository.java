package cz.ecis.db.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import cz.ecis.db.ent.CampEnt;


public interface CampRepository extends CampBaseRepository {

    @Query("""
        SELECT DISTINCT c FROM CampEnt c
        JOIN CampRoleEnt cr ON cr.camp.id = c.id
        WHERE cr.user.id = :userId AND cr.role.name = 'USER'
    """)
    List<CampEnt> findAllUserCamps(Long userId);
}
