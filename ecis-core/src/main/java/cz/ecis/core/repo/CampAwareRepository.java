package cz.ecis.core.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import cz.ecis.core.ent.ICampEntity;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.utils.CrudUtils;

@NoRepositoryBean
public interface CampAwareRepository<T extends ICampEntity, I extends Serializable> extends CrudRepository<T, I> {

    List<T> findAllByCampId(Long campId);

    default T findByIdAndCampId(I id, Long campId) {
        T ent = this.findById(id).orElseThrow(
            () -> new EntityNotExistsException("Record not found", id)
        );

        CrudUtils.checkEntityCamp(ent, campId);

        return ent;
    }
}