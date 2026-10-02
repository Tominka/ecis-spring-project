package cz.ecis.db.ent;

import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
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
public class ParentEnt extends VersionAwareEntity<Long> implements ILovEntity {

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

    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "parents")
    private List<ApplicationEnt> applications;

    @Override
    public String getLabel() {
        return name + " " + surname;
    }
}
