package cz.ecis.db.ent;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.RevisionType;
import org.hibernate.type.SqlTypes;

import cz.ecis.core.audit.EcisAuditId;
import cz.ecis.core.ent.lookup.EcisLookupTranslation;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "audit", name = "lkp_child_info_history")
@Getter
@Setter
public class LkpChildInfoHistoryEnt {

    @EmbeddedId
    private EcisAuditId pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_revision", nullable = false, insertable=false, updatable=false)
    private RevisionEnt revision;

    @Column(name = "revision_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RevisionType revisionType;

    @Column(name = "code", length = 10)
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, EcisLookupTranslation> translations = new HashMap<>();

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "version")
    private Integer version;
}
