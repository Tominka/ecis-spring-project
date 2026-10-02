package cz.ecis.db.ent;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        schema = "ecis",
        name = "application_parent",
        joinColumns = @JoinColumn(name = "id_application", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id_parent", nullable = false)
    )
    private List<ParentEnt> parents;

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    @Column(name = "canceled_at", nullable = false)
    private OffsetDateTime canceledAt;
}
