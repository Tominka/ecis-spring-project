package cz.ecis.db.ent;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "camp_role")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CampRoleEnt extends VersionAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", referencedColumnName = "id", updatable = false, insertable = false)
    private UserEnt user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", referencedColumnName = "id", updatable = false, insertable = false)
    private RoleEnt role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_camp", referencedColumnName = "id", updatable = false, insertable = false)
    private CampEnt camp;
}
