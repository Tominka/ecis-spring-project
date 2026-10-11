package cz.ecis.db.ent;

import java.util.UUID;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "ecis", name = "application_document")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ApplicationDocumentEnt extends VersionAwareEntity<UUID> {

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false, insertable = true)
    private ApplicationEnt application;

    @ManyToOne
    @JoinColumn(name = "id_document_type", nullable = false, insertable = true)
    private LkpDocumentTypeEnt documentType;

    @Column(name = "filename", nullable = false, length = 510)
    private String filename;

    @Column(name = "file_location", nullable = false, length = 2048)
    private String fileLocation;

    @Column(name = "file_size", nullable = false, length = 50)
    private String fileSize;
}
