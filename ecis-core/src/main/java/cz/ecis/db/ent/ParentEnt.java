package cz.ecis.db.ent;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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

    @OneToMany(
        mappedBy = "parent",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @NotAudited
    private List<ApplicationParentEnt> applications = new ArrayList<>();

    @Override
    public String getLabel() {
        return name + " " + surname;
    }
}
