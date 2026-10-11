package cz.ecis.db.ent;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "application_parent")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ApplicationParentEnt extends VersionAwareEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false)
    private ApplicationEnt application;

    @ManyToOne
    @JoinColumn(name = "id_parent", nullable = false)
    private ParentEnt parent;

}
