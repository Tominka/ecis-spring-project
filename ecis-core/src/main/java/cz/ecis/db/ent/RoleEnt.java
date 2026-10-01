package cz.ecis.db.ent;

import cz.ecis.core.ent.CreatorAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "role")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RoleEnt extends CreatorAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "global", nullable = false)
    private boolean global;
}
