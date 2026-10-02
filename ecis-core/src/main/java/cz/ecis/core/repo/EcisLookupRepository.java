package cz.ecis.core.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import cz.ecis.core.ent.lookup.EcisLookupEntity;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.utils.CrudUtils;

@NoRepositoryBean
public interface EcisLookupRepository<T extends EcisLookupEntity<I>, I extends Serializable> extends VersionAwareRepository<T, I> {
    
    @Query("""
        select e
        from #{#entityName} e
        where e.validFrom <= CURRENT_TIMESTAMP
          and (e.validTo is null or e.validTo > CURRENT_TIMESTAMP)
        """)
    List<T> findAllValid();
    
    default T findByIdAndVersionValid(I id, Integer version) {
        T ent = this.findByIdAndVersion(id, version);

        CrudUtils.checkLookupValidity(ent);

        return ent;
    }

    default T findByIdValid(I id) {
        T ent = this.findById(id).orElseThrow(
            () -> new EntityNotExistsException("Record not found", id)
        );

        CrudUtils.checkLookupValidity(ent);

        return ent;
    }
}
