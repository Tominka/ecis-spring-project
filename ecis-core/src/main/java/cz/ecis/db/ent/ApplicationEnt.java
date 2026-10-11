package cz.ecis.db.ent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "application")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class ApplicationEnt extends VersionAwareEntity<Long> {

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_child", referencedColumnName = "id", updatable = true, insertable = true, nullable = false)
    private ChildEnt child;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_camp", referencedColumnName = "id", updatable = true, insertable = true, nullable = false)
    private CampEnt camp;

    @OneToMany(
        mappedBy = "application",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @NotAudited
    private List<ApplicationParentEnt> parents = new ArrayList<>();

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    @Column(name = "canceled_at", nullable = false)
    private OffsetDateTime canceledAt;

    @OneToMany(
        mappedBy = "application",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @NotAudited
    private List<ApplicationDocumentEnt> documents = new ArrayList<>();
}
