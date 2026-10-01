package cz.ecis.core.ent.lookup;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import cz.ecis.core.ent.VersionAwareEntity;
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
public abstract class EcisLookupEntity<I extends Serializable> extends VersionAwareEntity<I> {
    
    private static final long serialVersionUID = 1L;

    @Column(name = "code", length = 50, nullable = true, insertable = true, updatable = true)
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, EcisLookupTranslation> translations = new HashMap<>();

    @Column(name = "valid_from", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = true, updatable = true)
    private OffsetDateTime validFrom;

    @Column(name = "valid_to", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = true, updatable = true)
    private OffsetDateTime validTo;

}