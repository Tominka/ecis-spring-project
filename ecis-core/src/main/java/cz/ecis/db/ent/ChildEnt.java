package cz.ecis.db.ent;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "child")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class ChildEnt extends VersionAwareEntity<Long> implements ILovEntity {
	
	private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "surname", length = 255, nullable = false)
    private String surname;

    @Column(name = "address", length = 510, nullable = false)
    private String address;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_camp", referencedColumnName = "id", updatable = true, insertable = true, nullable = false)
    private CampEnt camp;

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    @Column(name = "canceled_at", nullable = false)
    private OffsetDateTime canceledAt;

    @Override
    public String getLabel() {
        return name + " " + surname;
    }
}
