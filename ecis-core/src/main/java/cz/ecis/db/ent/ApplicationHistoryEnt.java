package cz.ecis.db.ent;

import java.time.OffsetDateTime;

import org.hibernate.envers.RevisionType;

import cz.ecis.core.audit.EcisAuditId;
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
@Table(schema = "audit", name = "application_history")
@Getter
@Setter
public class ApplicationHistoryEnt {

    @EmbeddedId
    private EcisAuditId pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_revision", nullable = false, insertable=false, updatable=false)
    private RevisionEnt revision;

    @Column(name = "revision_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private RevisionType revisionType;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "canceled_at")
    private OffsetDateTime canceledAt;

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