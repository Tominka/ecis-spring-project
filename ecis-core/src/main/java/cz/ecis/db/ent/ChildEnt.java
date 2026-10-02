package cz.ecis.db.ent;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
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

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "surname", length = 255, nullable = false)
    private String surname;

    @Column(name = "address", length = 510, nullable = false)
    private String address;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        schema = "ecis",
        name = "child_info",
        joinColumns = @JoinColumn(name = "id_child", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id", nullable = false)
    )
    private List<ChildInfoEnt> info;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    private List<ApplicationEnt> applications;

    @Override
    public String getLabel() {
        return name + " " + surname;
    }
}
