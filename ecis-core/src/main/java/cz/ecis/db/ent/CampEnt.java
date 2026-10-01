package cz.ecis.db.ent;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.envers.Audited;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "camp")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class CampEnt extends VersionAwareEntity<Long> implements ILovEntity {

	private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "location", length = 255, nullable = false)
    private String location;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name="date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name="date_to", nullable = true)
    private LocalDate dateTo;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name="archived_at", nullable = true)
    private OffsetDateTime archivedAt;

    @Override
    public String getLabel() {
        return this.name;
    }
}