package cz.ecis.db.ent;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;

import cz.ecis.core.ent.ICampEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "application_overview_vie")
@Immutable
@Getter
@Setter
public class ApplicationOverviewVieEnt implements ICampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_child", nullable = false)
    private ChildEnt child;

    @ManyToOne
    @JoinColumn(name = "id_camp", nullable = false)
    private CampEnt camp;

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    @Column(name = "canceled_at", nullable = false)
    private OffsetDateTime canceledAt;

    @Column(name = "document_count", nullable = false)
    private Long documentCount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}