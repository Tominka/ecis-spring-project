package cz.ecis.db.ent;

import java.util.Set;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.ICampEntity;
import cz.ecis.core.ent.ILovEntity;
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
@Table(schema = "ecis", name = "parent")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class ParentEnt extends VersionAwareEntity<Long> implements ILovEntity, ICampEntity {

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "surname", length = 255, nullable = false)
    private String surname;

    @Column(name = "address", length = 510, nullable = true)
    private String address;

    @Column(name = "phone", length = 19, nullable = true)
    private String phone;

    @Column(name = "email", length = 255, nullable = true)
    private String email;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_camp", referencedColumnName = "id", updatable = true, insertable = true, nullable = false)
    private CampEnt camp;

    // @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //     schema = "ecis",
    //     name = "child_parent",
    //     joinColumns = @JoinColumn(name = "id_parent"),
    //     inverseJoinColumns = @JoinColumn(name = "id_child")
    // )
    // private Set<ChildEnt> children;

    @Override
    public String getLabel() {
        return name + " " + surname;
    }
}
