package cz.ecis.core.ent;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public abstract class VersionAwareEntity<I extends Serializable> extends CreatorAwareEntity<I> {

    private static final long serialVersionUID = 1L;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50, nullable = true , insertable = false)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

}
