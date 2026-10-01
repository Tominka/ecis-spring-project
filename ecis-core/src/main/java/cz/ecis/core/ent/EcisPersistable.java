package cz.ecis.core.ent;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class EcisPersistable<I> implements Serializable, Persistable<I> {

    private static final long serialVersionUID = -1L;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
