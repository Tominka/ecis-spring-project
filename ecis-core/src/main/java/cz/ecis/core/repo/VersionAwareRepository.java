package cz.ecis.core.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import cz.ecis.core.ent.VersionAwareEntity;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.utils.CrudUtils;

@NoRepositoryBean
public interface VersionAwareRepository<T extends VersionAwareEntity<I>, I extends Serializable> extends JpaRepository<T, I> {

    default T findByIdAndVersion(I id, Integer version) {
        T ent = this.findById(id).orElseThrow(
            () -> new EntityNotExistsException("Record not found", id)
        );

        CrudUtils.checkEntityVersion(ent.getVersion(), version);

        return ent;
    }

}
