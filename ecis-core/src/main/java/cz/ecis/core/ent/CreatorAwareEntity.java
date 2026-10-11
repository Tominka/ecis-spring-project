package cz.ecis.core.ent;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public abstract class CreatorAwareEntity<I extends Serializable> extends EcisPersistable<I> {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, insertable = true, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 50, nullable = false, insertable = true, updatable = false)
    private String createdBy;
}