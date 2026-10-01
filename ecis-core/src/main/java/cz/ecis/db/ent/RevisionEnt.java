package cz.ecis.db.ent;

import java.io.Serializable;
import java.time.Instant;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import cz.ecis.core.audit.EcisRevisionListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "audit", name = "revision")
@Getter
@Setter
@RevisionEntity(EcisRevisionListener.class)
public class RevisionEnt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long id;

    @Column(name="created_at", updatable = true, insertable = true, nullable = false)
    @RevisionTimestamp
    private Instant createdAt;

    @Column(name="username", updatable = true, insertable = true, nullable = false)
    private String username;

    /**
     * API
     * BATCH
     * IMPORT
     */
    @Column(name="source", updatable = true, insertable = true, nullable = true)
    private String source;

    @Column(name="id_correlation", updatable = true, insertable = true, nullable = true)
    private String correlationId;
}